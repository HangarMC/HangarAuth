<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form :ui="modifiedUi" :title="t('verify.title')" />
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer, UiText } from "@ory/kratos-client/api";
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";
import { useAsyncData, useHead } from "#imports";
import { useKratos } from "~/plugins/kratos";

const { t } = useI18n();

const kratos = useKratos();
const { data } = useAsyncData<{ ui: UiContainer }>(
  "ui",
  async () =>
    await kratos.requestUiContainer(
      (flow, cookie) => kratos.client.getVerificationFlow(flow, cookie, { withCredentials: true }),
      kratos.verify.bind(kratos),
      kratos.verify.bind(kratos)
    )
);

const modifiedUi = computed(() => {
  if (data.value?.ui) {
    const node = data.value.ui.nodes.find((n) => n.group === "link");
    if (node) {
      node.meta.label = {
        text: "E-Mail",
      } as UiText;
    }
    return data.value.ui;
  }
  return data.value.ui;
});

useHead({
  title: t("verify.title"),
});
</script>
