<template>
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :required="node.attributes.required"
    :label="node.meta.label ? node.meta.label.text : null"
    :messages="messages"
    :error-messages="errorMessages"
    type="email"
    persistent-hint
    autocomplete="email"
  ></InputText>
</template>

<script lang="ts" setup>
import InputText from "~/lib/components/ui/InputText.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value } = useFormElement(props as Props);
</script>
