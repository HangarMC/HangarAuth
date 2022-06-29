<template>
  <img :id="node.attributes.id" :src="node.attributes.src" :height="node.attributes.height" :width="node.attributes.width" :alt="node.meta.label.text" />
</template>

<script lang="ts" setup>
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node } = useFormElement(props as Props);
</script>
