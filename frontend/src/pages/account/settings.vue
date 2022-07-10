<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <h1 class="py-2 text-xl mb-4 text-center rounded bg-gray" v-text="t('settings.title')" />
    <Form :title="t('settings.userinfo')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'profile']" />
    <Form :title="t('settings.password')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'password']" />
    <Form :title="t('settings.2fa')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'totp']" />
    <Form :title="t('settings.2fa-backup')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'lookup_secret']" />
    <Form :title="t('settings.webauthn')" disable-autocomplete :ui="data.ui" :include-groups="['default', 'webauthn']" />
    <Card class="mt-2">
      <h3 class="text-lg mb-2" v-text="t('settings.avatar.title')" />
      <div class="flex">
        <div class="basis-full md:basis-1/2">
          <div>current</div>
          <img :src="`/avatar/${store.user?.id}`" width="200" />
        </div>
      </div>
    </Card>
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer, UiNodeInputAttributes } from "@ory/kratos-client/api";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { useAuthStore } from "~/store/useAuthStore";
import AvatarChangeModal from "~/lib/components/modals/AvatarChangeModal.vue";

const { t } = useI18n();
const store = useAuthStore();
const { $kratos } = useNuxtApp();
const data = useState<{ ui: UiContainer; flowId: string }>("ui");
data.value = await $kratos.requestUiContainer(
  (flow, cookie) => $kratos.client.getSelfServiceSettingsFlow(flow, undefined, cookie, { withCredentials: true }),
  $kratos.settings.bind($kratos)
);

const file = ref();

const csrfToken = computed(() => {
  if (!data.value?.ui) {
    throw new Error("Must have UI to get a csrfToken");
  }
  const node = data.value.ui.nodes.find((n) => "name" in n.attributes && n.attributes.name === "csrf_token");
  if (!node) {
    throw new Error("No csrf token found");
  }
  return (node.attributes as UiNodeInputAttributes).value;
});

useHead({
  title: t("settings.title"),
});

await $kratos.loadUser();
</script>
