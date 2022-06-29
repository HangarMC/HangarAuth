import { RouteLocation } from "vue-router";

export function useFlow(route: RouteLocation, onNoFlow: () => void): string | null {
  if (!route.query.flow || Array.isArray(route.query.flow)) {
    console.debug("no flow", route.query);
    onNoFlow();
    return null;
  }
  return route.query.flow;
}
