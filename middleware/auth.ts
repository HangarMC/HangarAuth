import { Context, Middleware } from '@nuxt/types';

export function AuthRequired() {
    const middleware = ({ $kratos }: Context) => {
        return $kratos.client
            .toSession(undefined, undefined, { withCredentials: true })
            .then((session) => {
                console.log('whoami', session.data);
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
function addMiddleware(constructor: Function, ...middleware: Middleware[]): void {
    if (!constructor.prototype.middleware) {
        constructor.prototype.middleware = [...middleware];
    } else if (typeof constructor.prototype.middleware === 'string') {
        constructor.prototype.middleware = [constructor.prototype.middleware, ...middleware];
    } else if (Array.isArray(constructor.prototype.middleware)) {
        constructor.prototype.middleware = [...constructor.prototype.middleware, ...middleware];
    } else {
        throw new TypeError('Unable to add permissions middleware');
    }
}
