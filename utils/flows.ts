import { Route } from 'vue-router';

export function requireFlow(route: Route, onNoFlow: () => void): string | null {
    if (!route.query.flow || Array.isArray(route.query.flow)) {
        onNoFlow();
        return null;
    }
    return route.query.flow;
}
