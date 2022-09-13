import { useSettingsStore } from "~/store/useSettingsStore";
import { authLog } from "~/lib/composables/useLog";

export default defineNuxtRouteMiddleware(async (to) => {
  const loginRequired = to.meta?.loginRequired || false;
  authLog("middleware... loading user (login required " + loginRequired + ")", to.fullPath);
  await useNuxtApp().$kratos.loadUser(loginRequired);
  authLog("loaded");
  if (process.server) {
    const event = useNuxtApp().ssrContext?.event;
    if (event) {
      useSettingsStore().loadSettingsServer(event.req, event.res);
    } else {
      authLog("No request on server?!");
    }
  }
});
