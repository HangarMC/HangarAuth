import { fetchLog } from "~/lib/composables/useLog";

export function useInternalApi<T = void>(
  url: string,
  method: "get" | "post" | "put" | "delete" | "GET" | "POST" | "PUT" | "DELETE" = "get",
  data?: object,
  _args: object = {}
): Promise<T> {
  fetchLog("useInternalApi", url, data);
  return $fetch(url, {
    method,
    body: data,
    credentials: "include",
  });
}
