import { defineStore } from "pinia";
import type { Ref } from "vue";
import { computed, ref, unref } from "vue";
import { settingsLog } from "~/lib/composables/useLog";
import { useAuthStore } from "~/store/useAuthStore";
import { $fetch } from "ohmyfetch";
import { useSettingsHelper } from "~/lib/composables/useSettingsHelper";

export const useSettingsStore = defineStore("settings", () => {
  settingsLog("defineSettingsStore");
  const darkMode: Ref<boolean> = ref(false);
  const locale: Ref<string> = ref("en");

  const csrfToken = ref<string | undefined>();
  const flowId = ref<string | undefined>();

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

  const authStore = useAuthStore();
  const userData = computed(() => {
    return {
      hasUser: Boolean(authStore.user),
      theme: authStore.user?.traits?.theme,
      language: authStore.user?.traits?.language,
    };
  });

  async function saveSettings() {
    const store = useAuthStore();
    // we always want to save in order to set the cookie
    const user = store.user?.id || "anon";
    settingsLog("Save settings for", user);

    const form = new FormData();
    form.append("theme", darkMode.value ? "dark" : "light");
    form.append("language", locale.value);
    form.append("flowId", flowId.value || "");
    if (csrfToken.value) {
      form.append("csrf_token", csrfToken.value);
    }

    // TODO pass cookies
    let headers = {};
    if (process.server) {
      headers = { cookie: "" };
    }

    // TODO use url from env var (client prolly doesn't even need a proxy, so only backend)
    const url = `http://localhost:3001/settings/${user}`;
    console.log("url", url);
    try {
      await $fetch(url, {
        method: "POST",
        body: form,
        credentials: "include",
        headers,
      });
    } catch (e) {
      settingsLog("Can't save settings", e);
    }
  }

  const { loadSettingsServer, loadSettingsClient } = useSettingsHelper(
    process.server,
    userData,
    () => useCookie("HANGAR_theme").value,
    (loc) => (locale.value = loc),
    (dark) => (darkMode.value = dark),
    saveSettings,
    darkMode
  );

  return {
    darkMode,
    toggleDarkMode,
    enableDarkMode,
    disableDarkMode,
    loadSettingsServer,
    loadSettingsClient,
    locale,
    csrfToken,
    flowId,
  };
});
