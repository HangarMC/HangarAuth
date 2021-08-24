import { V0alpha1ApiFactory } from '@ory/kratos-client';
import { Context } from '@nuxt/types';
import { Inject } from '@nuxt/types/app';

const createKratos = ({ $axios, redirect }: Context) => {
    class Kratos {
        get client() {
            // @ts-ignore
            return V0alpha1ApiFactory({ basePath: process.env.kratos }, process.env.kratos, $axios);
        }

        login() {
            redirect(process.env.kratos + '/self-service/registration/browser');
        }

        register() {
            redirect(process.env.kratos + '/self-service/login/browser');
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
