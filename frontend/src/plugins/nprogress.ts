import NProgress from "nprogress";
import { nextTick } from "vue";
import { defineNuxtPlugin, useRouter } from "#imports";

export default defineNuxtPlugin(() => {
  if (!process.server) {
    useRouter().beforeEach(() => {
      NProgress.start();
    });
    useRouter().afterEach(async () => {
      await nextTick(() => NProgress.done());
    });
  }
});
