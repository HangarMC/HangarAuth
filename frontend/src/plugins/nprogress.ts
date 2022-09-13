import NProgress from "nprogress";
import { nextTick } from "vue";
import { defineNuxtPlugin } from "#imports";

export default defineNuxtPlugin((nuxtApp) => {
  if (!process.server) {
    nuxtApp.$router.beforeEach(() => {
      NProgress.start();
    });
    nuxtApp.$router.afterEach(async () => {
      await nextTick(() => NProgress.done());
    });
  }
});
