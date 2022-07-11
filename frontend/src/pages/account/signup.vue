<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <h1 class="py-2 text-xl mb-4 text-center rounded bg-gray" v-text="t('signup.title')" />
    <form :method="data.ui.method" :action="data.ui.action">
      <Form
        :ui="data.ui"
        :title="t('signup.userInfo')"
        disable-autocomplete
        :include-groups="['default']"
        :fields="['traits.username', 'traits.email']"
        no-form
      />
      <Form
        :ui="data.ui"
        :title="t('signup.optionalInfo')"
        disable-autocomplete
        :include-groups="['default']"
        fields-as-excludes
        :fields="['traits.username', 'traits.email']"
        no-form
      />
      <Form :ui="data.ui" :title="t('signup.credentials')" disable-autocomplete :include-groups="['password', 'webauthn']" no-form />
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
