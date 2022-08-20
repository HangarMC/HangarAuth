<template>
  <!-- todo messages and error messages -->
  <Button
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :messages="messages"
    :error-messages="errorMessages"
    :value="value"
    size="medium"
    button-type="primary"
    type="submit"
  >
    {{ text }}
  </Button>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import Button from "~/lib/components/design/Button.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value } = useFormElement(props as Props);

const text = computed(() => (node.meta.label ? node.meta.label.text : "submit"));
</script>
