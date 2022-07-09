<template>
  <!-- todo messages, error-messages -->
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :label="node.meta.label ? node.meta.label.text : null"
    :messages="messages"
    :error-messages="errorMessages"
    :rules="rules"
    type="text"
    :autocomplete="autocomplete"
  />
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import InputText from "~/lib/components/ui/InputText.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value, rules } = useFormElement(props as Props);

const autocomplete = computed(() => {
  switch ((node.attributes as UiNodeInputAttributes).name) {
    case "password_identifier":
    case "traits.username":
      return "username";
    case "traits.name.first":
      return "given-name";
    case "traits.name.last":
      return "family-name";
    case "traits.language":
      return "language";
  }
  return undefined;
});
</script>

<style scoped></style>
