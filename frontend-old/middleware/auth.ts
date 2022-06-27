import { Context, Middleware } from '@nuxt/types';
import { addMiddleware } from '~/utils/middleware';

export function AuthRequired() {
    const middleware: Middleware = ({ $kratos, store }: Context) => {
        console.debug('fetching session...');
        return $kratos.client
            .toSession(undefined, undefined, { withCredentials: true })
            .then((session) => {
                console.debug('got result', session.data);
                if (session.data && session.data.active) {
                    store.commit('SET_USER', session.data.identity);
                    store.commit('SET_AAL', { aal: session.data.authenticator_assurance_level, methods: session.data.authentication_methods });
                    return;
                }
                console.debug('no session -> login');
                return $kratos.login();
            })
            .catch((e: any) => {
                if (e.response) {
                    if (e.response.data.redirect_browser_to) {
                        console.debug('session catch: url', e.response.data.redirect_browser_to);
                        return $kratos.redirect(e.response.data.redirect_browser_to);
                    } else if (e.response.status === 401) {
                        console.debug('session catch: 401 -> login');
                        return $kratos.login();
                    } else if (e.response.status === 404) {
                        console.debug('session catch: 403 -> aal');
                        return $kratos.aal2();
                    }
                }
                console.error('session catch:', e);
            });
    };

    return function (constructor: Function) {
        addMiddleware(constructor, middleware);
    };
}
