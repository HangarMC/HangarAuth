<template>
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :label="node.meta.label ? node.meta.label.text : undefined"
    :type="show ? 'text' : 'password'"
    :messages="messages"
    :error-messages="errorMessages"
    :rules="rules"
    no-error-tooltip
    :autocomplete="disableAutocomplete ? 'new-password' : 'current-password'"
  >
    <template #append>
      <IconMdiEye v-if="show" @click="show = false" />
      <IconMdiEyeOff v-else @click="show = true" />
    </template>
  </InputText>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import InputText from "~/lib/components/ui/InputText.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, disableAutocomplete, value, rules } = useFormElement<UiNodeInputAttributes>(props as Props);

const show = ref(false);
</script>
