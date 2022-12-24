<template>
  <div>
    <!-- this is a hardcoded way to display secrets differently -->
    <template v-if="secrets">
      <div>{{ node.meta.label?.text }}</div>
      <div class="flex flex-wrap mt-2 mb-2">
        <div v-for="secret in secrets" :key="secret.id" class="basis-3/12">
          <!-- 1050014 means used -->
          <code>{{ secret.id === 1050014 ? "Used" : secret.text }}</code>
        </div>
      </div>
    </template>
    <template v-else>
      <div>{{ node.meta.label?.text }}</div>
      <div>{{ node.attributes.text.text }}</div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { UiNodeTextAttributes } from "@ory/kratos-client/api";
import { formProps, Props, useFormElement } from "~/composables/useFormElement";

const props = defineProps(formProps());
const { node } = useFormElement<UiNodeTextAttributes>(props as Props);

const secrets = computed(() => {
  return (node.attributes.text.context as any)?.secrets;
});
</script>
