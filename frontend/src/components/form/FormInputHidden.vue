<template>
  <input :name="node.attributes.name" :value="node.attributes.value" type="hidden" required />
</template>

<script lang="ts" setup>
import { UiNodeInputAttributes } from "@ory/kratos-client";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node } = useFormElement<UiNodeInputAttributes>(props as Props);
</script>
