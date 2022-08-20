<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form :title="t('login.title')" :ui="data.ui" :tabs="tabs">
      <template #additional-buttons>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.register()">Register</Button>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.reset()">Forgot</Button>
      </template>
    </Form>
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer } from "@ory/kratos-client/api";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { FormTab } from "~/components/form/FormContainer.vue";

const { t } = useI18n();
const { $kratos } = useNuxtApp();
const { data } = useAsyncData<{ ui: UiContainer }>(
  "ui",
  async () =>
    await $kratos.requestUiContainer((flow, cookie) =>
      $kratos.client.getSelfServiceLoginFlow(flow, cookie, {
        withCredentials: true,
      })
    )
);

const file = ref();

const tabs = computed<FormTab[]>(() => {
  return [
    { value: "password", header: "Password", groups: ["default", "password"] },
    { value: "key", header: "Security key", groups: ["default", "webauthn"] },
    { value: "totp", header: "Authenticator", groups: ["totp"] },
    { value: "backup", header: "Backup", groups: ["lookup_secret"] },
  ];
});

useHead({
  title: t("login.title"),
});
</script>
