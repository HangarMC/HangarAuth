import { UiContainer, V0alpha1ApiFactory } from '@ory/kratos-client';
import { Context } from '@nuxt/types';
import { Inject } from '@nuxt/types/app';
import { V0alpha1Api } from '@ory/kratos-client/api';
import { AxiosError, AxiosResponse } from 'axios';
import { requireFlow } from '~/utils/flows';

function _redirect(url: string, redirect: Context['redirect']) {
    console.log('redirect to: ' + url);
    if (process.server) {
        redirect(url);
    } else {
        window.location.href = url;
    }
}

const createKratos = ({ $axios, redirect, route }: Context) => {
    class Kratos {
        get client(): V0alpha1Api {
            const url = process.server ? process.env.kratos : process.env.kratosPublic;
            // @ts-ignore
            return V0alpha1ApiFactory({ basePath: url }, url, $axios);
        }

        login() {
            try {
                _redirect(process.env.kratosPublic + '/self-service/login/browser', redirect);
            } catch (e) {}
        }

        register() {
            try {
                _redirect(process.env.kratosPublic + '/self-service/registration/browser', redirect);
            } catch (e) {}
        }

        reset() {
            try {
                _redirect(process.env.kratosPublic + '/self-service/recovery/browser', redirect);
            } catch (e) {}
        }

        verify() {
            try {
                _redirect(process.env.kratosPublic + '/self-service/verification/browser', redirect);
            } catch (e) {}
        }

        settings() {
            try {
                _redirect(process.env.kratosPublic + '/self-service/settings/browser', redirect);
            } catch (e) {}
        }

        logout() {
            this.client.createSelfServiceLogoutFlowUrlForBrowsers(undefined, { withCredentials: true }).then((url) => {
                _redirect(url.data.logout_url as string, redirect);
            });
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

        requestUiContainer(
            fetchFlow: (flow: string) => Promise<AxiosResponse<{ ui: UiContainer; id: string }>>,
            onNoFlow: () => void = this.login,
            onErrRedirect: () => void = this.login
        ): Promise<void | { ui: UiContainer }> | void {
            const flow = requireFlow(route, onNoFlow);
            if (flow) {
                return fetchFlow(flow)
                    .then((flowInfo) => {
                        console.debug(flowInfo.data.ui.nodes);
                        return { ui: flowInfo.data.ui, flowId: flowInfo.data.id };
                    })
                    .catch(this.redirectOnError(onErrRedirect));
            }
        }
    }

    return new Kratos();
};

type kratosType = ReturnType<typeof createKratos>;

declare module 'vue/types/vue' {
    interface Vue {
        $kratos: kratosType;
    }
}

declare module '@nuxt/types' {
    interface NuxtAppOptions {
        $kratos: kratosType;
    }

    interface Context {
        $kratos: kratosType;
    }
}

declare module 'vuex/types/index' {
    // eslint-disable-next-line no-unused-vars,@typescript-eslint/no-unused-vars
    interface Store<S> {
        $kratos: kratosType;
    }
}

export default (ctx: Context, inject: Inject) => {
    inject('kratos', createKratos(ctx));
};
