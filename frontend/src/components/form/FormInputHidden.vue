<template>
  <input v-bind="node.attributes" />
</template>

<script lang="ts" setup>
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node } = useFormElement(props as Props);
</script>
