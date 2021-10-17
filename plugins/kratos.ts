import { V0alpha1ApiFactory } from '@ory/kratos-client';
import { Context } from '@nuxt/types';
import { Inject } from '@nuxt/types/app';
import { V0alpha1Api } from '@ory/kratos-client/api';

const createKratos = ({ $axios, redirect }: Context) => {
    class Kratos {
        get client(): V0alpha1Api {
            const url = process.server ? process.env.kratos : process.env.kratosPublic;
            // @ts-ignore
            return V0alpha1ApiFactory({ basePath: url }, url, $axios);
        }

        login() {
            try {
                this._redirect(process.env.kratosPublic + '/self-service/login/browser');
            } catch (e) {}
        }

        register() {
            try {
                this._redirect(process.env.kratosPublic + '/self-service/registration/browser');
            } catch (e) {}
        }

        reset() {
            try {
                this._redirect(process.env.kratosPublic + '/self-service/recovery/browser');
            } catch (e) {}
        }

        verify() {
            try {
                this._redirect(process.env.kratosPublic + '/self-service/verification/browser');
            } catch (e) {}
        }

        settings() {
            try {
                this._redirect(process.env.kratosPublic + '/self-service/settings/browser');
            } catch (e) {}
        }

        logout() {
            this.client.createSelfServiceLogoutFlowUrlForBrowsers(undefined, { withCredentials: true }).then((url) => {
                this._redirect(url.data.logout_url as string);
            });
        }

        _redirect(url: string) {
            if (process.server) {
                redirect(url);
            } else {
                window.location.href = url;
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
