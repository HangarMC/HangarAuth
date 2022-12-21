<template>
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
  <div v-if="messages || errorMessages" class="inline-block ml-2">
    <span v-if="messages" class="text-small">
      <span v-for="message in messages" :key="message"> {{ message }}<br /> </span>
    </span>
    <span v-if="errorMessages" class="text-small text-red-400">
      <span v-for="message in errorMessages" :key="message"> {{ isErrorObject(message) ? message.$message : message }}<br /> </span>
    </span>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { UiNodeInputAttributes } from "@ory/kratos-client/api";
import { isErrorObject } from "~/lib/composables/useValidationHelpers";
import Button from "~/lib/components/design/Button.vue";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node, messages, errorMessages, disabledField, value } = useFormElement<UiNodeInputAttributes>(props as Props);

const text = computed(() => (node.meta.label ? node.meta.label.text : "submit"));
</script>
