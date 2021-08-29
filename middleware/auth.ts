import { Context, Middleware } from '@nuxt/types';
import { addMiddleware } from '~/utils/middleware';

export function AuthRequired() {
    const middleware: Middleware = ({ $kratos, store }: Context) => {
        return $kratos.client
            .toSession(undefined, undefined, { withCredentials: true })
            .then((session) => {
                if (session.data && session.data.active) {
                    store.commit('SET_USER', session.data.identity);
                    return;
                }
                return $kratos.login();
            })
            .catch((e) => {
                if (e.response.status === 401) {
                    return $kratos.login();
                }
                console.log(e);
            });
    };

    return function (constructor: Function) {
        addMiddleware(constructor, middleware);
    };
}
