<template>
  <component :is="noForm ? 'div' : 'form'" v-if="!empty" :method="noForm ? null : ui.method" :action="noForm ? null : ui.action">
    <Card class="mt-2">
      <h1 class="text-xl mb-4">{{ title }}</h1>
      <div class="flex flex-wrap gap-2">
        <FormContainer :nodes="filteredNodes" :disable-autocomplete="disableAutocomplete" :disabled-fields="disabledFields" :tabs="tabs" />
        <slot name="additional-buttons" />
      </div>
    </Card>
  </component>
</template>

<script lang="ts" setup>
import { UiNode } from "@ory/kratos-client";
import { computed } from "vue";
import { UiContainer } from "@ory/kratos-client/api";
import Card from "~/lib/components/design/Card.vue";
import FormContainer, { FormTab } from "~/components/form/FormContainer.vue";

const props = withDefaults(
  defineProps<{
    title: string;
    includeGroups?: string[];
    fields?: string[];
    fieldsAsExcludes?: boolean;
    disabledFields?: string[];
    ui: UiContainer;
    disableAutocomplete?: boolean;
    noForm?: boolean;
    tabs?: FormTab[];
  }>(),
  {
    includeGroups: () => [],
    fields: () => [],
    disabledFields: () => [],
    fieldsAsExcludes: false,
    disableAutocomplete: false,
    noForm: false,
    tabs: () => [],
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

const empty = computed(() => {
  if (filteredNodes.value.length === 0) {
    return true;
  }
  if (filteredNodes.value.length === 1) {
    return (filteredNodes.value[0].attributes as any).name === "csrf_token";
  }
  return false;
});
</script>

<style scoped></style>
