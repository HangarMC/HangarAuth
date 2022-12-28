<template>
  <div>
    <Alert v-if="!verified" type="info" class="mb-2 cursor-pointer">
      <a class="block" @click="kratos.verify()">
        Your account is not verified yet, check your email!<br />
        Haven't received an email? Click here to resend!
      </a>
    </Alert>
    <Alert v-if="verified && !aal2" type="info" class="mb-2"> You haven't set up 2FA yet!</Alert>
    <Alert v-if="newAccount" type="info" class="mb-2">
      <a :href="runtimeConfig.public.hangarHost + '/login?returnUrl=/'">Account created! Click here to go to Hangar!</a>
    </Alert>
    <Card v-if="data && data.ui">
      <UserMessages :ui="data.ui" />
      <h1 class="py-2 text-xl mb-4 text-center rounded bg-gray" v-text="t('settings.title')" />
      <div class="flex gap-2 flex-wrap md:flex-nowrap">
        <div class="basis-full md:basis-8/12 flex-shrink">
          <!-- todo: username changing isn't implemented, so block here -->
          <!-- todo: language and theme need a nice selector, so hide them for now -->
          <Form
            :title="t('settings.userinfo')"
            disable-autocomplete
            :ui="data.ui"
            :include-groups="['default', 'profile']"
            fields-as-excludes
            :fields="['traits.theme', 'traits.language']"
          />
          <Form :title="t('settings.password')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'password']" />
          <Form :title="t('settings.2fa-backup')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'lookup_secret']" />
          <Form :title="t('settings.webauthn')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'webauthn']" />
        </div>
        <div class="basis-full md:basis-4/12">
          <Card class="mt-2">
            <h3 class="text-lg mb-2" v-text="t('settings.avatar.title')" />
            <img :src="`/avatar/${store.user?.id}`" width="200" class="mb-2" alt="Avatar" />
            <AvatarChangeModal :csrf-token="csrfToken" :avatar="`/avatar/${store.user?.id}`" :action="`/avatar/${store.user?.id}?flowId=${data.flowId}`" />
          </Card>
          <Form :title="t('settings.2fa')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'totp']" />
        </div>
      </div>
    </Card>
  </div>
</template>

<script lang="ts" setup>
import { type UiNodeInputAttributes } from "@ory/kratos-client/api";
import { useI18n } from "vue-i18n";
import { type UiContainer, UiNode } from "@ory/kratos-client";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { useAuthStore } from "~/store/useAuthStore";
import AvatarChangeModal from "~/lib/components/modals/AvatarChangeModal.vue";
import Alert from "~/lib/components/design/Alert.vue";
import { useSettingsStore } from "~/store/useSettingsStore";
import { computed, definePageMeta, useAsyncData, useHead, useRuntimeConfig, watch } from "#imports";
import { useKratos } from "~/plugins/kratos";

definePageMeta({
  loginRequired: true,
});

const { t } = useI18n();
const store = useAuthStore();
const kratos = useKratos();
const { data } = useAsyncData<{ ui: UiContainer }>(
  "ui",
  async () =>
    await kratos.requestUiContainer(
      (flow: string, cookie: string | undefined) => kratos.client.getSettingsFlow(flow, undefined, cookie, { withCredentials: true }),
      kratos.settings.bind(kratos)
    )
);

const authStore = useAuthStore();
const settingsStore = useSettingsStore();
const runtimeConfig = useRuntimeConfig();

const csrfToken = computed(() => {
  if (!data.value?.ui) {
    throw new Error("Must have UI to get a csrfToken");
  }
  const node = data.value.ui.nodes.find((n: UiNode) => "name" in n.attributes && n.attributes.name === "csrf_token");
  if (!node) {
    throw new Error("No csrf token found");
  }
  return (node.attributes as UiNodeInputAttributes).value;
});

const csrfTokenLenient = computed(() => {
  if (!data.value?.ui) {
    return null;
  }
  const node = data.value.ui.nodes.find((n: UiNode) => "name" in n.attributes && n.attributes.name === "csrf_token");
  if (!node) {
    return null;
  }
  return (node.attributes as UiNodeInputAttributes).value;
});

watch(csrfTokenLenient, (newVal) => (settingsStore.csrfToken = newVal), { immediate: true });
watch(data, (newVal) => (settingsStore.flowId = newVal?.flowId), { immediate: true });

const verified = computed(() => {
  const user = authStore.user;
  if (!user || !user.verifiable_addresses) {
    return false;
  }
  for (const verifiableAddress of user.verifiable_addresses) {
    if (verifiableAddress.verified) {
      return true;
    }
  }
  return false;
});

const aal2 = computed(() => {
  if (verified.value) {
    return authStore.aal?.aal === "aal2";
  }
  return false;
});

const newAccount = computed(() => data.value?.requestUrl?.includes("new=true"));

useHead({
  title: t("settings.title"),
});
</script>
