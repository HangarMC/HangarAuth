<template>
  <!-- todo messages and error messages -->
  <Button
    :disabled="disabledField || node.attributes.disabled"
    :name="node.attributes.name"
    :messages="messages"
    :error-messages="errorMessages"
    :value="node.attributes.value || ''"
    button-type="secondary"
    size="medium"
    :onclick="node.attributes.onclick"
    @click.prevent
  >
    {{ text }}
  </Button>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import Button from "~/lib/components/design/Button.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField } = useFormElement(props as Props);

const text = computed(() => (node.meta.label ? node.meta.label.text : "submit"));
</script>
