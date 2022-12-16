<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script lang="ts" setup>
import "./lib/styles/main.css";
import { useSettingsStore } from "~/store/useSettingsStore";
import { useAuthStore } from "~/store/useAuthStore";
import { settingsLog } from "~/lib/composables/useLog";
import { computed, useHead } from "#imports";

const authStore = useAuthStore();
const settingsStore = useSettingsStore();
settingsStore.loadSettingsClient();
settingsLog("render for user", authStore.user?.traits?.username, "with darkmode", settingsStore.darkMode);
useHead({
  htmlAttrs: {
    class: computed(() => (settingsStore.darkMode ? "dark" : "light")),
  },
  bodyAttrs: {
    class: "background-body text-[#262626] dark:text-[#E0E6f0]",
  },
});
</script>
