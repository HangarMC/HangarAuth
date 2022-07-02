<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form :title="t('login.title')" :ui="data.ui">
      <template #additional-buttons>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.register()">Register</Button>
        <Button button-type="secondary" size="medium" @click.prevent="$kratos.reset()">Forgot</Button>
      </template>
    </Form>
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer } from "@ory/kratos-client/api";
import { ref } from "vue";
import { useHead, useNuxtApp, useState } from "nuxt/app";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";

const { t } = useI18n();
const { $kratos } = useNuxtApp();
const data = useState<{ ui: UiContainer }>("ui");
data.value = await $kratos.requestUiContainer((flow, cookie) => $kratos.client.getSelfServiceLoginFlow(flow, cookie, { withCredentials: true }));

const file = ref();

useHead({
  title: t("login.title"),
});
</script>
