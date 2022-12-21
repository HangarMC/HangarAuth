<script setup lang="ts">
import { Popover, PopoverButton, PopoverPanel } from "@headlessui/vue";
import { useI18n } from "vue-i18n";
import { type Component, ComputedRef } from "vue";
import hangarLogo from "~/lib/assets/hangar-logo.svg";
import IconMdiHome from "~icons/mdi/home";
import IconMdiAccountGroup from "~icons/mdi/account-group";
import IconMdiForum from "~icons/mdi/forum";
import IconMdiCodeBraces from "~icons/mdi/code-braces";
import IconMdiBookOpen from "~icons/mdi/book-open";
import IconMdiLanguageJava from "~icons/mdi/language-java";
import IconMdiDownloadCircle from "~icons/mdi/download-circle";
import IconMdiPuzzle from "~icons/mdi/puzzle";
import Button from "~/lib/components/design/Button.vue";
import { useAuthStore } from "~/store/useAuthStore";
import { useSettingsStore } from "~/store/useSettingsStore";
import { computed, useRuntimeConfig } from "#imports";

const { t } = useI18n();
const authStore = useAuthStore();
const settings = useSettingsStore();
const runtimeConfig = useRuntimeConfig();

type NavLink = {
  link: string;
  label: string;
  icon: Component;
};

// if we are logged in, we can trigger a login
const hangarLink = computed(() =>
  authStore.user ? `${runtimeConfig.public.hangarHost}/login?returnUrl=${runtimeConfig.public.hangarHost}` : runtimeConfig.public.hangarHost
);

const navBarMenuLinksMoreFromPaper: ComputedRef<NavLink[]> = computed(() => [
  { link: "https://papermc.io/", label: t("nav.hangar.home"), icon: IconMdiHome },
  { link: "https://forums.papermc.io/", label: t("nav.hangar.forums"), icon: IconMdiForum },
  { link: "https://github.com/PaperMC", label: t("nav.hangar.code"), icon: IconMdiCodeBraces },
  { link: "https://docs.papermc.io/", label: t("nav.hangar.docs"), icon: IconMdiBookOpen },
  { link: "https://papermc.io/javadocs", label: t("nav.hangar.javadocs"), icon: IconMdiLanguageJava },
  { link: "https://papermc.io/downloads", label: t("nav.hangar.downloads"), icon: IconMdiDownloadCircle },
  { link: "https://papermc.io/community", label: t("nav.hangar.community"), icon: IconMdiAccountGroup },
  { link: hangarLink.value, label: t("nav.hangar.hangar"), icon: IconMdiPuzzle },
]);
</script>

<template>
  <header class="background-default shadow-md">
    <nav class="max-w-screen-xl mx-auto flex justify-between px-4 py-2">
      <!-- Left side items -->
      <div class="flex items-center gap-4">
        <Popover class="relative">
          <PopoverButton v-slot="{ open }" class="flex">
            <icon-mdi-menu class="transition-transform text-[1.2em]" :class="open ? 'transform rotate-90' : ''" />
          </PopoverButton>
          <transition
            enter-active-class="transition duration-200 ease-out"
            enter-from-class="translate-y-1 opacity-0"
            enter-to-class="translate-y-0 opacity-100"
            leave-active-class="transition duration-150 ease-in"
            leave-from-class="translate-y-0 opacity-100"
            leave-to-class="translate-y-1 opacity-0"
          >
            <!-- dummy diff to make the transition work on pages where template root has multiple elements -->
            <div id="#navbarMenuLinks">
              <PopoverPanel
                class="fixed z-10 w-9/10 background-default top-1/14 left-1/20 filter drop-shadow-md rounded-md border-top-primary text-sm p-[20px]"
                md="absolute w-max top-10 rounded-none rounded-bl-md rounded-r-md"
              >
                <div class="grid grid-cols-2">
                  <a
                    v-for="link in navBarMenuLinksMoreFromPaper"
                    :key="link.label"
                    class="flex items-center rounded-md px-6 py-2"
                    :href="link.link"
                    hover="text-primary-400 bg-primary-0"
                  >
                    <component :is="link.icon" class="mr-3 text-[1.2em]" />
                    {{ link.label }}
                  </a>
                </div>
              </PopoverPanel>
            </div>
          </transition>
        </Popover>

        <!-- Site logo -->
        <router-link to="/">
          <img alt="Hangar Logo" :src="hangarLogo" class="h-8" />
        </router-link>
      </div>

      <!-- Right side items -->
      <div class="flex items-center gap-2">
        <!-- todo dark mode -->
        <button class="flex rounded-md p-2" hover="text-primary-400 bg-primary-0" @click="settings.toggleDarkMode()">
          <icon-mdi-weather-night v-if="settings.darkMode" class="text-[1.2em]"></icon-mdi-weather-night>
          <icon-mdi-white-balance-sunny v-else class="text-[1.2em]"></icon-mdi-white-balance-sunny>
        </button>
        <Button v-if="authStore.user" button-type="primary" size="small" @click="$kratos.logout()">{{ t("general.logout") }}</Button>
      </div>
    </nav>
  </header>
</template>

<style lang="css" scoped>
nav .router-link-active {
  @apply color-primary;
  font-weight: 700;
}

nav a.router-link-active:after {
  background: linear-gradient(-270deg, #004ee9 0%, #367aff 100%);
  transition: width 0.2s ease-in;
  width: 80%;
}

nav a:not(.router-link-active):hover:after {
  background: #d3e1f6;
  transition: width 0.2s ease-in;
  width: 80%;
}
</style>
