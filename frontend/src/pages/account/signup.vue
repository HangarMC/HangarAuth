<template>
  <Card v-if="!signupDisabled && data && data.ui">
    <UserMessages :ui="data?.ui" />
    <h1 class="py-2 text-xl mb-4 text-center rounded bg-gray" v-text="t('signup.title')" />
    <form :method="data?.ui.method" :action="actionWithInvite">
      <!-- user info for normal, username and email -->
      <Form
        :ui="data?.ui"
        :title="t('signup.userInfo')"
        disable-autocomplete
        :include-groups="['default', 'password']"
        :fields="['traits.username', 'traits.email']"
        no-form
      />
      <!-- user info is in password if no webauthn -->
      <!-- todo we don't have nice ui for language and theme, nor do they do much, so just disable for now -->
      <Form
        :ui="data?.ui"
        :title="t('signup.optionalInfo')"
        disable-autocomplete
        :include-groups="['default', 'password']"
        :fields="['traits.github', 'traits.discord']"
        no-form
      />
      <!-- oicd (user info is part of oidc group) -->
      <Form
        :ui="data?.ui"
        :title="t('signup.userInfo')"
        disable-autocomplete
        :include-groups="['oidc']"
        no-form
        :disabled-fields="['traits.email']"
        :fields="['traits.username', 'traits.email']"
      />
      <Form
        :ui="data?.ui"
        :title="t('signup.openid')"
        disable-autocomplete
        :include-groups="['oidc']"
        no-form
        fields-as-excludes
        :fields="['traits.username', 'traits.email']"
      />
      <!-- creds -->
      <Form
        :ui="data?.ui"
        :title="t('signup.credentials')"
        disable-autocomplete
        :include-groups="['password', 'webauthn', 'default']"
        :fields="['password', 'method', 'webauthn.*', 'csrf_token']"
        no-form
        :tabs="credentialsTabs"
      />
    </form>
  </Card>
  <Card v-else-if="signupDisabled">
    <h1 class="text-xl mb-4">Signup is currently disabled!</h1>
    <p>Come back at a later time please.</p>
  </Card>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { UiContainer } from "@ory/kratos-client/api";
import { computed } from "vue";
import { UiNode } from "@ory/kratos-client";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { FormTab } from "~/components/form/FormContainer.vue";
import { useKratos } from "~/plugins/kratos";
import { useAsyncData, useHead, useRoute, useRuntimeConfig } from "#imports";

const { t } = useI18n();

const route = useRoute();
const config = useRuntimeConfig();
const signupDisabled = config.public.signupDisabled;
const kratos = useKratos();
const { data } = useAsyncData<{ ui: UiContainer; request_url: string }>(
  "ui",
  async () =>
    await kratos.requestUiContainer(
      (flow, cookie) => kratos.client.getRegistrationFlow(flow, cookie, { withCredentials: true }),
      kratos.register.bind(kratos, route.query.invite ? "?invite=" + route.query.invite : ""),
      kratos.register.bind(kratos, route.query.invite ? "?invite=" + route.query.invite : "")
    )
);

const credentialsTabs = computed<FormTab[]>(() => {
  // no webauth no tabs
  if (!data.value?.ui.nodes.find((n: UiNode) => n.group === "webauthn")) return [];
  return [
    { value: "password", header: "Password", groups: ["password"] },
    { value: "key", header: "Security key", groups: ["webauthn"] },
  ];
});

const actionWithInvite = computed(() => {
  const url = data.value?.request_url;
  const invite = new URL(url)?.searchParams?.get("invite");
  return data.value?.ui.action + (invite ? "&invite=" + invite : "");
});

useHead({
  title: t("signup.title"),
});
</script>
