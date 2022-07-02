import { defineNuxtPlugin } from "nuxt/app";
import { installI18n } from "~/lib/i18n";

export default defineNuxtPlugin(async (nuxtApp) => {
  // TODO load from settings
  await installI18n(nuxtApp.vueApp, "en");
});
