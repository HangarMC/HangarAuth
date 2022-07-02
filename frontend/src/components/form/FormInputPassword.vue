<template>
  <!-- todo icon, messages, error-messages -->
  <InputText
    v-model="value"
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :required="node.attributes.required"
    :label="node.meta.label ? node.meta.label.text : null"
    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
    :type="show ? 'text' : 'password'"
    :messages="messages"
    :error-messages="errorMessages"
    persistent-hint
    :autocomplete="disabledAutocomplete ? 'new-password' : 'current-password'"
    @click:append="show = !show"
  ></InputText>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import InputText from "~/lib/components/ui/InputText.vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, disabledAutocomplete, value } = useFormElement(props as Props);

const show = ref(false);
</script>
