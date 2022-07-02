<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form :ui="data.ui" :title="t('signup.title')" disable-autocomplete />
  </Card>
  <Card v-else-if="signupDisabled">
    <h1 class="text-xl mb-4">Signup is currently disabled!</h1>
    <p>Come back at a later time please.</p>
  </Card>
</template>

<script lang="ts" setup>
import { useHead, useNuxtApp, useRuntimeConfig, useState } from "nuxt/app";
import { useI18n } from "vue-i18n";
import { UiContainer } from "@ory/kratos-client/api";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";

const { t } = useI18n();

const config = useRuntimeConfig();
const signupDisabled = config.public.signupDisabled;
const { $kratos } = useNuxtApp();
const data = useState<{ ui: UiContainer }>("ui");
if (!signupDisabled) {
  data.value = await $kratos.requestUiContainer(
    (flow, cookie) => $kratos.client.getSelfServiceRegistrationFlow(flow, cookie, { withCredentials: true }),
    $kratos.register.bind($kratos),
    $kratos.register.bind($kratos)
  );
}

useHead({
  title: t("signup.title"),
});
</script>
