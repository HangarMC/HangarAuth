<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form v-if="isWebAuthnLogin" title="Device ready?" :ui="data.ui" />
    <Form v-else-if="is2fa" title="Second Factor" :ui="data.ui" />
    <Form v-else :title="t('login.title')" :ui="data.ui" :tabs="tabs">
      <template #additional-buttons>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.register()">Register</Button>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.reset()">Forgot</Button>
      </template>
    </Form>
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer } from "@ory/kratos-client/api";
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { FormTab } from "~/components/form/FormContainer.vue";
import { useAsyncData, useHead } from "#imports";
import { useKratos } from "~/plugins/kratos";

const { t } = useI18n();
const kratos = useKratos();
const { data } = useAsyncData<null | { ui: UiContainer }>(
  "ui",
  async () =>
    await kratos.requestUiContainer((flow, cookie) =>
      kratos.client.getLoginFlow(flow, cookie, {
        withCredentials: true,
      })
    )
);

const tabs = computed<FormTab[]>(() => {
  return [
    { value: "password", header: "Password", groups: ["default", "password"] },
    { value: "key", header: "Security key", groups: ["default", "webauthn"] },
    { value: "totp", header: "Authenticator", groups: ["totp"] },
    { value: "backup", header: "Backup", groups: ["lookup_secret"] },
  ];
});

const is2fa = computed(() => data.value?.ui.messages?.[0]?.id === 1010004);
const isWebAuthnLogin = computed(() => data.value?.ui.messages?.[0]?.id === 1010012);

useHead({
  title: t("login.title"),
});
</script>
