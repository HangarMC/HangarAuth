import { Identity } from "@ory/kratos-client/api";
import { defineStore } from "pinia";
import { ref } from "vue";
import { AALInfo } from "~/plugins/kratos";

export const useAuthStore = defineStore("auth", () => {
  const user = ref<Identity | null>(null);
  const aal = ref<AALInfo | null>(null);
  const hydraData = ref<string[]>([]);

  return { user, aal, hydraData };
});
