<template>
  <form v-if="filteredNodes.length > 0" :method="ui.method" :action="ui.action">
    <Card class="mt-2">
      <h1 class="text-xl mb-4">{{ title }}</h1>
      <div class="flex flex-wrap gap-2">
        <component
          :is="'form-' + node.type + (node.attributes.type && !node.attributes.type.includes('/') ? '-' + node.attributes.type : '')"
          v-for="(node, idx) in filteredNodes"
          :key="idx"
          :node="node"
          :disable-autocomplete="disableAutocomplete"
          :disabled-field="disabledFields.includes(node.attributes.name)"
          class="basis-full"
        />
        <slot name="additional-buttons" />
      </div>
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
    fields?: string[];
    fieldsAsExcludes?: boolean;
    disabledFields?: string[];
    ui: UiContainer;
    disableAutocomplete?: boolean;
  }>(),
  {
    includeGroups: () => [],
    fields: () => [],
    disabledFields: () => [],
    fieldsAsExcludes: false,
    disableAutocomplete: false,
  }
);

const filteredNodes = computed<UiNode[]>(() => {
  if (!props.ui.nodes) {
    return [];
  }
  if (props.includeGroups.length === 0 && props.fields.length === 0) {
    return props.ui.nodes;
  }
  return props.ui.nodes.filter(
    (n) =>
      (props.includeGroups.length === 0 || props.includeGroups.includes(n.group)) &&
      (props.fields.length === 0 || props.fieldsAsExcludes
        ? !props.fields.includes((n.attributes as any).name)
        : props.fields.includes((n.attributes as any).name))
  );
});
</script>

<style scoped></style>
