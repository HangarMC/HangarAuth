import { RouteMiddleware } from "nuxt/app";
import { useSettingsStore } from "~/store/useSettingsStore";
import { authLog } from "~/lib/composables/useLog";
import { defineNuxtRouteMiddleware, useNuxtApp } from "#imports";
import { useKratos } from "~/plugins/kratos";

export default defineNuxtRouteMiddleware((async (to) => {
  const loginRequired = (to.meta?.loginRequired as boolean) || false;
  authLog("middleware... loading user (login required " + loginRequired + ")", to.fullPath);
  await useKratos().loadUser(loginRequired);
  authLog("loaded");
  if (process.server) {
    const event = useNuxtApp().ssrContext?.event;
    if (event) {
      useSettingsStore().loadSettingsServer(event.node.req, event.node.res);
    } else {
      authLog("No request on server?!");
    }
  }
}) as RouteMiddleware);
