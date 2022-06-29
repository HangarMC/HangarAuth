<template>
  <div>
    <Alert v-for="message in ui.messages" :key="message.id" :type="message.type">
      {{ message.text }}
    </Alert>
  </div>
</template>

<script lang="ts" setup>
import { UiContainer } from "@ory/kratos-client/api";
import Alert from "~/lib/components/design/Alert.vue";

defineProps<{
  ui: UiContainer;
}>();
</script>
