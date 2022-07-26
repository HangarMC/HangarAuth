import { defineStore } from "pinia";
import type { Ref } from "vue";
import { ref, unref, watch } from "vue";
import { settingsLog } from "~/lib/composables/useLog";
import localeParser from "accept-language-parser";
import { SUPPORTED_LOCALES } from "~/lib/i18n";
import { useAuthStore } from "~/store/useAuthStore";

// TODO somehow share this with hangar, right now its copy pasted and adjusted
export const useSettingsStore = defineStore("settings", () => {
  settingsLog("defineSettingsStore");
  const darkMode: Ref<boolean> = ref(false);
  const locale: Ref<string> = ref("en");

  const mobile: Ref<boolean> = ref(true); // True cause mobile first!!
  const mobileBreakPoint = 700;

  function toggleDarkMode() {
    darkMode.value = !unref(darkMode);
    settingsLog("darkmode", darkMode.value);
  }

  function enableDarkMode() {
    darkMode.value = true;
    settingsLog("darkmode", darkMode.value);
  }

  function disableDarkMode() {
    darkMode.value = false;
    settingsLog("darkmode", darkMode.value);
  }

  function toggleMobile() {
    mobile.value = !unref(mobile);
  }

  function enableMobile() {
    mobile.value = true;
  }

  function disableMobile() {
    mobile.value = false;
  }

  // TODO actually call this
  function loadSettingsServer(request: any, response: any) {
    if (!import.meta.env.SSR) return;
    const authStore = useAuthStore();
    let newLocale;
    let theme;
    if (authStore.user) {
      newLocale = authStore.user.traits.language || "en";
      theme = authStore.user.traits.theme || "white";
      settingsLog("user is logged in, locale = " + newLocale + ", theme = " + theme);
    } else {
      if (request.headers["accept-language"]) {
        const pickedLocale = localeParser.pick(SUPPORTED_LOCALES, request.headers["accept-language"]);
        if (!pickedLocale) {
          settingsLog("user is not logged in and could not pick locale from header, using default...", SUPPORTED_LOCALES, request.headers["accept-language"]);
          newLocale = "en";
        } else {
          settingsLog("user is not logged in, picking from locale header, locale = " + pickedLocale, SUPPORTED_LOCALES, request.headers["accept-language"]);
          newLocale = pickedLocale;
        }
      } else {
        settingsLog("using default locale cause there was no header...");
        newLocale = "en";
      }

      response?.setHeader("Accept-CH", "Sec-CH-Prefers-Color-Scheme");
      response?.setHeader("Vary", "Sec-CH-Prefers-Color-Scheme");
      response?.setHeader("Critical-CH", "Sec-CH-Prefers-Color-Scheme");
      const themeHeader = request.headers["sec-ch-prefers-color-scheme"];
      if (themeHeader) {
        settingsLog("user is not logged in, using theme from header", themeHeader);
        theme = themeHeader;
      } else {
        settingsLog("user is not logged in and we got no theme header, using default...", themeHeader);
      }
    }

    locale.value = newLocale;
    if (theme === "white" || theme === "light") {
      disableDarkMode();
    } else {
      enableDarkMode();
    }
    // TODO save into traits
    // const obj = {
    //   theme: darkMode.value ? "dark" : "light",
    //   language: locale.value,
    // };
  }

  watch(darkMode, (newMode) => {
    if (import.meta.env.SSR) return;
    if (newMode) {
      settingsLog("set dark");
      localStorage.theme = "dark";
      document.documentElement.classList.add("dark");
      document.documentElement.classList.remove("light");
    } else {
      settingsLog("set light");
      localStorage.theme = "light";
      document.documentElement.classList.add("light");
      document.documentElement.classList.remove("dark");
    }
    // TODO save into traits
    // const obj = {
    //   theme: darkMode.value ? "dark" : "light",
    //   language: locale.value,
    // };
  });

  function loadSettingsClient() {
    if (import.meta.env.SSR) return;

    let darkMode = localStorage.theme === "dark" || (!("theme" in localStorage) && window.matchMedia("(prefers-color-scheme: dark)").matches);
    const user = useAuthStore().user;
    if (user && user.traits.theme) {
      darkMode = user.traits.theme === "dark";
    }

    if (darkMode) {
      enableDarkMode();
    } else {
      disableDarkMode();
    }

    // For checking if on mobile or not
    if (innerWidth <= mobileBreakPoint && !mobile.value) {
      enableMobile();
    } else if (innerWidth > mobileBreakPoint && mobile) {
      disableMobile();
    }
    addEventListener("resize", () => {
      if (innerWidth <= mobileBreakPoint && !mobile.value) {
        enableMobile();
      } else if (innerWidth > mobileBreakPoint && mobile) {
        disableMobile();
      }
    });
  }

  return {
    darkMode,
    toggleDarkMode,
    enableDarkMode,
    disableDarkMode,
    mobile,
    toggleMobile,
    enableMobile,
    disableMobile,
    mobileBreakPoint,
    loadSettingsServer,
    loadSettingsClient,
    locale,
  };
});
