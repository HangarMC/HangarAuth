<template>
  <div>
    <Alert v-for="message in ui.messages" :key="message.id" :type="type(message.type)">
      {{ message.text }}
    </Alert>
  </div>
</template>

<script lang="ts" setup>
import { UiContainer } from "@ory/kratos-client/api";
import Alert from "~/lib/components/design/Alert.vue";

const props = defineProps<{
  ui: UiContainer;
}>();

function type(alertType: string) {
  if (alertType === "error") return "danger";
  if (alertType === "info" || alertType === "success" || alertType === "warning") return alertType;
  return "info";
}
</script>
