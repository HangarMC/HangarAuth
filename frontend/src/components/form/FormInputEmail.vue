<template>
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :label="node.meta.label ? node.meta.label.text : undefined"
    :messages="messages"
    :error-messages="errorMessages"
    :rules="rules"
    type="email"
    persistent-hint
    no-error-tooltip
    autocomplete="email"
  ></InputText>
</template>

<script lang="ts" setup>
import { UiNodeInputAttributes } from "@ory/kratos-client";
import InputText from "~/lib/components/ui/InputText.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value, rules } = useFormElement<UiNodeInputAttributes>(props as Props);
</script>
