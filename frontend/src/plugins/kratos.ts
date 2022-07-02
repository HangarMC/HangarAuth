import { UiContainer, V0alpha2ApiFactory } from "@ory/kratos-client";
import { AuthenticatorAssuranceLevel, SessionAuthenticationMethod, V0alpha2Api } from "@ory/kratos-client/api";
import axios, { AxiosError, AxiosResponse } from "axios";
import { defineNuxtPlugin, useRoute, useRuntimeConfig } from "nuxt/app";
import { CompatibilityEvent, sendRedirect } from "h3";
import { useFlow } from "~/composables/useFlow";
import { useAuthStore } from "~/store/useAuthStore";

export interface AALInfo {
  aal: AuthenticatorAssuranceLevel;
  methods: Array<SessionAuthenticationMethod>;
}

export class Kratos {
  kratosUrl: string;
  kratosPublicUrl: string;
  event: CompatibilityEvent | null;

  constructor(kratosUrl: string, kratosPublicUrl: string, event: CompatibilityEvent | null) {
    this.kratosUrl = kratosUrl;
    this.kratosPublicUrl = kratosPublicUrl;
    this.event = event;
  }

  get client(): V0alpha2Api {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return V0alpha2ApiFactory({ basePath: this.kratosUrl }, this.kratosUrl, axios);
  }

  async redirect(url: string) {
    console.log("redirect to: " + url);
    if (process.server) {
      await sendRedirect(this.event!, url);
    } else {
      window.location.href = url;
    }
  }

  async login() {
    try {
      await this.redirect(this.kratosUrl + "/self-service/login/browser");
    } catch (e) {
      console.log(e);
    }
  }

  async aal2() {
    try {
      await this.redirect(this.kratosUrl + "/self-service/login/browser?aal=aal2");
    } catch (e) {
      console.log(e);
    }
  }

  async register() {
    try {
      await this.redirect(this.kratosUrl + "/self-service/registration/browser");
    } catch (e) {
      console.log(e);
    }
  }

  async reset() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/recovery/browser");
    } catch (e) {
      console.log(e);
    }
  }

  async verify() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/verification/browser");
    } catch (e) {
      console.log(e);
    }
  }

  async settings() {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/settings/browser");
    } catch (e) {
      console.log(e);
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
      console.error(err.message);
      console.error(err.response?.data);
      if (err.response) {
        if (err.response.status === 404 || err.response.status === 410 || err.response.status === 403) {
          return redirect();
        }
      }
    };
  }

  async requestUiContainer(
    fetchFlow: (flow: string, cookie: unknown) => Promise<AxiosResponse<{ ui: UiContainer; id: string }>>,
    onNoFlow: () => void = this.login.bind(this),
    onErrRedirect: () => void = this.login.bind(this)
  ): Promise<null | { ui: UiContainer; flowId: string }> {
    const flow = useFlow(useRoute(), onNoFlow);
    if (flow) {
      try {
        const flowInfo = await fetchFlow(flow, this.event ? this.event.req.headers.cookie : undefined);
        console.debug(flowInfo.data.ui.nodes);
        return { ui: flowInfo.data.ui, flowId: flowInfo.data.id };
      } catch (e) {
        console.debug("redirectOnError", e.response?.data ? e.response.data : e);
        this.redirectOnError(onErrRedirect)(e);
        return null;
      }
    }
    return null;
  }

  async loadUser() {
    try {
      const session = await this.client.toSession(undefined, this.event ? this.event.req.headers.cookie : undefined, { withCredentials: true });

      console.debug("got result", session.data);
      if (session.data && session.data.active) {
        const authStore = useAuthStore();
        authStore.user = session.data.identity;
        authStore.aal = {
          aal: session.data.authenticator_assurance_level!,
          methods: session.data.authentication_methods!,
        };
        return;
      }
      console.debug("no session -> login");
      return this.login();
    } catch (e) {
      if (e.response) {
        if (e.response.data.redirect_browser_to) {
          console.debug("session catch: url", e.response.data.redirect_browser_to);
          return this.redirect(e.response.data.redirect_browser_to);
        } else if (e.response.status === 401) {
          console.debug("session catch: 401 -> login");
          return this.login();
        } else if (e.response.status === 404) {
          console.debug("session catch: 403 -> aal");
          return this.aal2();
        }
      }
      console.error("session catch:", e);
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
        process.server ? nuxtApp.ssrContext?.event : null
      ),
    },
  };
});

declare module "#app" {
  interface NuxtApp {
    $kratos: typeof Kratos;
  }
}

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $kratos: typeof Kratos;
  }
}
