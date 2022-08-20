<template>
  <Card v-if="data && data.ui">
    <UserMessages :ui="data.ui" />
    <Form :ui="modifiedUi" :title="t('reset.title')" />
  </Card>
</template>

<script lang="ts" setup>
import { UiContainer, UiText } from "@ory/kratos-client/api";
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";
import Form from "~/components/form/Form.vue";
import UserMessages from "~/components/UserMessages.vue";

const { t } = useI18n();

const { $kratos } = useNuxtApp();
const { data } = useAsyncData<{ ui: UiContainer }>(
  "ui",
  async () =>
    await $kratos.requestUiContainer(
      (flow, cookie) => $kratos.client.getSelfServiceRecoveryFlow(flow, cookie, { withCredentials: true }),
      $kratos.reset.bind($kratos),
      $kratos.reset.bind($kratos)
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
  title: t("reset.title"),
});
</script>
