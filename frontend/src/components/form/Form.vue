<template>
  <form v-if="filteredNodes.length > 0" :method="ui.method" :action="ui.action">
    <Card class="mt-2">
      <h1>{{ title }}</h1>
      <component
        :is="'form-' + node.type + (node.attributes.type && !node.attributes.type.includes('/') ? '-' + node.attributes.type : '')"
        v-for="(node, idx) in filteredNodes"
        :key="idx"
        :node="node"
        :disable-autocomplete="disableAutocomplete"
        :disabled-field="disabledFields.includes(node.attributes.name)"
      />
      <slot name="additional-buttons" />
    </Card>
  </form>
</template>

<script lang="ts" setup>
import { UiNode } from "@ory/kratos-client";
import { computed } from "vue";
import { UiContainer } from "@ory/kratos-client/api";
import Card from "~/lib/components/design/Card.vue";

const props = withDefaults(
  defineProps<{
    title: string;
    includeGroups?: string[];
    disabledFields?: string[];
    ui: UiContainer;
    disableAutocomplete?: boolean;
  }>(),
  {
    includeGroups: () => [],
    disabledFields: () => [],
    disableAutocomplete: false,
  }
);

const filteredNodes = computed<UiNode[]>(() => {
  if (!props.ui.nodes) {
    return [];
  }
  if (!props.includeGroups || props.includeGroups.length === 0) {
    return props.ui.nodes;
  }
  return props.ui.nodes.filter((n) => props.includeGroups.includes(n.group));
});
</script>

<style scoped></style>
