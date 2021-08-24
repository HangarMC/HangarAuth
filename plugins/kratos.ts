import { V0alpha1ApiFactory } from '@ory/kratos-client';
import { Context } from '@nuxt/types';
import { Inject } from '@nuxt/types/app';
import { V0alpha1Api } from '@ory/kratos-client/api';

const createKratos = ({ $axios, redirect }: Context) => {
    class Kratos {
        get client(): V0alpha1Api {
            // @ts-ignore
            return V0alpha1ApiFactory({ basePath: process.env.kratos }, process.env.kratos, $axios);
        }

        login() {
            try {
                redirect(process.env.kratos + '/self-service/login/browser');
            } catch (e) {}
        }

        register() {
            try {
                redirect(process.env.kratos + '/self-service/registration/browser');
            } catch (e) {}
        }

        reset() {
            try {
                redirect(process.env.kratos + '/self-service/recovery/browser');
            } catch (e) {}
        }

        logout() {
            this.client.createSelfServiceLogoutFlowUrlForBrowsers(undefined, { withCredentials: true }).then((url) => redirect(url.data.logout_url as string));
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
