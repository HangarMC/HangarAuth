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
        <!--TODO this is just base functionality: add image preview, prettify-->
        <div class="basis-full md:basis-1/2">
          <div>current</div>
          <img :src="`/avatar/${store.user.id}`" width="200" />
        </div>
        <div class="basis-full md:basis-1/2">
          <form method="POST" :action="`/avatar/${store.user.id}?flowId=${data.flowId}`" enctype="multipart/form-data">
            <div>new</div>
            <input type="hidden" name="csrf_token" :value="csrfToken" />
            <InputFile
              v-model="file"
              name="avatar"
              prepend-icon="mdi-camera"
              :placeholder="t('settings.avatar.inputPlaceholder')"
              :rules="required"
              accept="image/png,image/jpeg"
            />
            <Button class="mt-2" button-type="primary" type="submit" :disabled="!file" v-text="t('general.save')" />
          </form>
        </div>
      </div>
    </Card>
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer, UiNodeInputAttributes } from "@ory/kratos-client/api";
import { computed, ref } from "vue";
import { useHead, useNuxtApp, useState } from "nuxt/app";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import InputFile from "~/lib/components/ui/InputFile.vue";
import { required } from "~/lib/composables/useValidationHelpers";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { useAuthStore } from "~/store/useAuthStore";

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
