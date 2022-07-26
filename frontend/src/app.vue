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

const authStore = useAuthStore();
const settingsStore = useSettingsStore();
settingsStore.loadSettingsClient();
settingsLog("render for user", authStore.user?.traits?.username, "with darkmode", settingsStore.darkMode);
useHead({
  htmlAttrs: {
    class: settingsStore.darkMode ? "dark" : "light",
  },
});
</script>
