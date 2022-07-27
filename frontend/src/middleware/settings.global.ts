import { useSettingsStore } from "~/store/useSettingsStore";
import { authLog } from "~/lib/composables/useLog";

export default defineNuxtRouteMiddleware(async (to) => {
  authLog("middleware... loading user");
  const loginRequired = to.meta?.loginRequired || false;
  await useNuxtApp().$kratos.loadUser(loginRequired);
  authLog("loaded");
  if (process.server) {
    const event = useNuxtApp().ssrContext?.event;
    if (event) {
      await useSettingsStore().loadSettingsServer(event.req, event.res);
    } else {
      authLog("No request on server?!");
    }
  }
});
