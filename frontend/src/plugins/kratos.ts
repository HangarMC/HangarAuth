import * as https from "https";
import { UiContainer, V0alpha2ApiFactory } from "@ory/kratos-client";
import { AuthenticatorAssuranceLevel, SessionAuthenticationMethod, V0alpha2Api } from "@ory/kratos-client/api";
import axios, { AxiosError, AxiosResponse } from "axios";
import { type H3Event, sendRedirect } from "h3";
import { defineNuxtPlugin, useRoute, useRuntimeConfig } from "#imports";
import { useFlow } from "~/composables/useFlow";
import { useAuthStore } from "~/store/useAuthStore";
import { kratosLog } from "~/lib/composables/useLog";

export interface AALInfo {
  aal: AuthenticatorAssuranceLevel;
  methods: Array<SessionAuthenticationMethod>;
}

export class Kratos {
  kratosUrl: string;
  kratosPublicUrl: string;
  event: H3Event | null;

  constructor(kratosUrl: string, kratosPublicUrl: string, event: H3Event | null) {
    this.kratosUrl = kratosUrl;
    this.kratosPublicUrl = kratosPublicUrl;
    this.event = event;
  }

  get client(): V0alpha2Api {
    let instance = axios.create();
    if (process.server) {
      instance = axios.create({
        httpsAgent: new https.Agent({
          rejectUnauthorized: false,
        }),
      });
    }

    // @ts-ignore
    return V0alpha2ApiFactory({ basePath: this.kratosUrl }, this.kratosUrl, instance);
  }

  async redirect(url: string) {
    kratosLog("redirect to: " + url);
    if (process.server) {
      await sendRedirect(this.event!, url);
    } else {
      window.location.href = url;
    }
  }

  async login() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/login/browser");
    } catch (e) {
      kratosLog(e);
    }
  }

  async aal2() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/login/browser?aal=aal2");
    } catch (e) {
      kratosLog(e);
    }
  }

  async register() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/registration/browser");
    } catch (e) {
      kratosLog(e);
    }
  }

  async reset() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/recovery/browser");
    } catch (e) {
      kratosLog(e);
    }
  }

  async verify() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/verification/browser");
    } catch (e) {
      kratosLog(e);
    }
  }

  async settings() {
    try {
      const route = useRoute();
      let query = "";
      if (route.query.new) {
        query = "?new=true";
      }
      await this.redirect(this.kratosPublicUrl + "/self-service/settings/browser" + query);
    } catch (e) {
      kratosLog(e);
    }
  }

  logout() {
    this.client.createSelfServiceLogoutFlowUrlForBrowsers(undefined, { withCredentials: true }).then(async (url) => {
      await this.redirect(url.data.logout_url as string);
    });
  }

  async getErrorDetails(id: string) {
    const response = await this.client.getSelfServiceError(id);
    return response.data.error || { code: 500, message: "Unknown error occurred" };
  }

  redirectOnError(redirect: () => void): (err: AxiosError) => void {
    return (err) => {
      kratosLog(err.message);
      kratosLog(err.response?.data);
      if (err.response) {
        if (err.response.status === 404 || err.response.status === 410 || err.response.status === 403) {
          return redirect();
        }
      }
    };
  }

  async requestUiContainer(
    fetchFlow: (flow: string, cookieHeader: string | undefined) => Promise<AxiosResponse<{ ui: UiContainer; id: string; request_url: string }>>,
    onNoFlow: () => void = this.login.bind(this),
    onErrRedirect: () => void = this.login.bind(this)
  ): Promise<null | { ui: UiContainer; flowId: string; requestUrl: string }> {
    const flow = useFlow(useRoute(), onNoFlow);
    if (flow) {
      try {
        kratosLog("cookie header", this.event ? this.event.req.headers.cookie : undefined);
        const flowInfo = await fetchFlow(flow, this.event ? this.event.req.headers.cookie : undefined);
        kratosLog(flowInfo.data.ui.nodes);
        return { ui: flowInfo.data.ui, flowId: flowInfo.data.id, requestUrl: flowInfo.data.request_url };
      } catch (e: any) {
        const { request, ...err } = e;
        kratosLog("redirectOnError", e.response?.data ? e.response.data : err);
        this.redirectOnError(onErrRedirect)(e);
        return null;
      }
    }
    return null;
  }

  async loadUser(shouldRedirect = false) {
    try {
      const session = await this.client.toSession(undefined, this.event ? this.event.req.headers.cookie : undefined, { withCredentials: true });

      kratosLog("got result", session.data);
      if (session.data && session.data.active) {
        const authStore = useAuthStore();
        authStore.user = session.data.identity;
        authStore.aal = {
          aal: session.data.authenticator_assurance_level!,
          methods: session.data.authentication_methods!,
        };
        return;
      }
      kratosLog("no session -> login", shouldRedirect);
      return !shouldRedirect || this.login();
    } catch (e: any) {
      if (e.response) {
        if (e.response.data?.redirect_browser_to) {
          kratosLog("session catch: url", e.response.data.redirect_browser_to, shouldRedirect);
          return !shouldRedirect || this.redirect(e.response.data.redirect_browser_to);
        } else if (e.response.status === 401) {
          kratosLog("session catch: 401 -> login", shouldRedirect);
          return !shouldRedirect || this.login();
        } else if (e.response.status === 403) {
          kratosLog("session catch: 403 -> aal", shouldRedirect);
          return !shouldRedirect || this.aal2();
        }
      }

      const { config, request, ...err } = e;
      kratosLog("session catch:", Object.keys(err).length > 0 ? err : e, shouldRedirect);
      return !shouldRedirect || this.login();
    }
  }
}

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig();
  return {
    provide: {
      kratos: new Kratos(
        process.server ? config.kratos : config.public.kratosPublic,
        config.public.kratosPublic,
        process.server ? nuxtApp.ssrContext?.event || null : null
      ),
    },
  };
});

declare module "#app" {
  interface NuxtApp {
    $kratos: Kratos;
  }
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $kratos: Kratos;
  }
}
