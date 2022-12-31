import { AxiosRequestConfig } from "axios";
import { fetchLog } from "~/lib/composables/useLog";

export function useInternalApi<T = void>(url: string, method: AxiosRequestConfig["method"] = "get", data?: object): Promise<T> {
  fetchLog("useInternalApi", url, data);
  return $fetch(url, {
    method,
    body: data,
    credentials: "include",
  });
}
