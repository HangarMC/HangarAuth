import * as https from "https";
import { FrontendApiFactory, Session, UiContainer } from "@ory/kratos-client";
import { AuthenticatorAssuranceLevel, FlowError, FrontendApiFp, LogoutFlow, SessionAuthenticationMethod } from "@ory/kratos-client/api";
import axios, { AxiosError, AxiosInstance, AxiosPromise, AxiosResponse } from "axios";
import { type H3Event, sendRedirect } from "h3";
import { NuxtApp } from "nuxt/app";
import { defineNuxtPlugin, useInternalApi, useNuxtApp, useRoute, useRuntimeConfig } from "#imports";
import { useFlow } from "~/composables/useFlow";
import { useAuthStore } from "~/store/useAuthStore";
import { fetchLog, kratosLog } from "~/lib/composables/useLog";

export interface AALInfo {
  aal: AuthenticatorAssuranceLevel;
  methods: Array<SessionAuthenticationMethod>;
}

export function useKratos() {
  return useNuxtApp().$kratos as Kratos;
}

export class Kratos {
  kratosUrl: string;
  kratosPublicUrl: string;
  event: H3Event | null;
  backendHost: string;

  constructor(kratosUrl: string, kratosPublicUrl: string, event: H3Event | null, backendHost: string) {
    this.kratosUrl = kratosUrl;
    this.kratosPublicUrl = kratosPublicUrl;
    this.event = event;
    this.backendHost = backendHost;
  }

  private createClient(url: string): ReturnType<typeof FrontendApiFp> {
    let instance = axios.create();
    if (process.server) {
      instance = axios.create({
        httpsAgent: new https.Agent({
          rejectUnauthorized: false,
        }),
      });
    }

    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    return FrontendApiFactory({ basePath: url }, url, instance);
  }

  get client(): ReturnType<typeof FrontendApiFp> {
    return this.createClient(this.kratosUrl);
  }

  get proxyClient(): ReturnType<typeof FrontendApiFp> {
    return this.createClient("http://localhost:3001");
  }

  async redirect(url: string) {
    kratosLog("redirect to: " + url);
    if (process.server) {
      if (!this.event) throw new Error("Expecting an event to be present");
      await sendRedirect(this.event, url);
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

  async register(query?: string) {
    try {
      await this.redirect(this.kratosPublicUrl + "/self-service/registration/browser" + (query || ""));
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

  async logout() {
    const response = (await this.client.createBrowserLogoutFlow(undefined, { withCredentials: true })) as unknown as AxiosResponse<LogoutFlow>;
    await this.redirect(response.data.logout_url);
  }

  async getErrorDetails(id: string) {
    const response = (await this.client.getFlowError(id)) as unknown as AxiosResponse<FlowError>;
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
    fetchFlow: (
      flow: string,
      cookieHeader: string | undefined
    ) => Promise<(axios?: AxiosInstance, basePath?: string) => AxiosPromise<{ ui: UiContainer; id: string }>>,
    onNoFlow: () => void = this.login.bind(this),
    onErrRedirect: () => void = this.login.bind(this)
  ): Promise<null | { ui: UiContainer; flowId: string; request_url: string }> {
    const flow = useFlow(useRoute(), onNoFlow);
    if (flow) {
      try {
        const cookieHeader = this.event ? this.event.node.req.headers.cookie : undefined;
        kratosLog("fetch flow", flow, "cookie header", cookieHeader);
        const flowInfo = (await fetchFlow(flow, cookieHeader)) as unknown as AxiosResponse<{ ui: UiContainer; id: string; request_url: string }>;
        kratosLog(flowInfo.data.ui.nodes);
        return { ui: flowInfo.data.ui, flowId: flowInfo.data.id, request_url: flowInfo.data.request_url };
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
    const authStore = useAuthStore();
    // if we have a user from the backend, we can just return on the client
    if (authStore.user) {
      return;
    }
    try {
      // in dev on client we use a localhost:3001 and a proxy since cors for kratos doesn't seem to work locally...
      const client = process.server || process.env.NODE_ENV === "production" ? this.client : this.proxyClient;
      const session = (await client.toSession(undefined, this.event ? this.event.node.req.headers.cookie : undefined, {
        withCredentials: true,
      })) as unknown as AxiosResponse<Session>;

      kratosLog("load user result", session.data);
      let avatarUrl = "";
      if (session.data && session.data.active) {
        try {
          avatarUrl = await useInternalApi<string>(this.backendHost + "/avatar/user/" + session.data.identity.id);
        } catch (e) {
          fetchLog("Error loading avatar url ", e);
        }
        authStore.user = { ...session.data.identity, avatarUrl };
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
        // store the error, might be interesting
        if (e.response.data) {
          authStore.error = e.response.data;
        }

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

export default defineNuxtPlugin((nuxtApp: NuxtApp) => {
  const config = useRuntimeConfig();
  return {
    provide: {
      kratos: new Kratos(
        process.server ? config.kratos : config.public.kratosPublic,
        config.public.kratosPublic,
        process.server ? nuxtApp.ssrContext?.event || null : null,
        config.backendHost || ""
      ),
    },
  };
});

declare module "@vue/runtime-core" {
  interface ComponentCustomProperties {
    $kratos: Kratos;
  }
}
