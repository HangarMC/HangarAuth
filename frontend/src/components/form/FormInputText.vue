<template>
  <!-- todo messages, error-messages -->
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :label="label"
    :messages="messages"
    :error-messages="errorMessages"
    :rules="rules"
    type="text"
    no-error-tooltip
    :autocomplete="autocomplete"
  />
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import InputText from "~/lib/components/ui/InputText.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value, rules } = useFormElement<UiNodeInputAttributes>(props as Props);

const autocomplete = computed(() => {
  switch ((node.attributes as UiNodeInputAttributes).name) {
    case "password_identifier":
    case "traits.username":
      return "username";
    case "traits.language":
      return "language";
  }
  return undefined;
});

const label = computed(() => {
  if (node.meta.label) {
    // nobody understands what 'ID' means, lets override
    return node.meta.label.id === 1070004 ? "Username or email address" : node.meta.label.text;
  }
  return undefined;
});
</script>

<style scoped></style>
