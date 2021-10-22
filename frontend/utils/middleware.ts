import { Middleware } from '@nuxt/types';

export function addMiddleware(constructor: Function, ...middleware: Middleware[]): void {
    if (!constructor.prototype.middleware) {
        constructor.prototype.middleware = [...middleware];
    } else if (typeof constructor.prototype.middleware === 'string') {
        constructor.prototype.middleware = [constructor.prototype.middleware, ...middleware];
    } else if (Array.isArray(constructor.prototype.middleware)) {
        constructor.prototype.middleware = [...constructor.prototype.middleware, ...middleware];
    } else {
        throw new TypeError('Unable to add middleware');
    }
}
