import { defineNuxtPlugin } from "nuxt/app";
import { createI18n } from "vue-i18n";

// TODO i18n
export default defineNuxtPlugin((nuxtApp) => {
  const i18n = createI18n({
    legacy: false,
  });
  nuxtApp.vueApp.use(i18n);
});
