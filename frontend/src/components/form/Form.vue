<template>
  <component
    :is="noForm ? 'div' : 'form'"
    v-if="!empty"
    ref="form"
    :method="noForm ? null : ui.method"
    :action="noForm ? null : ui.action"
    @keydown.enter="submit"
  >
    <Card class="mt-2">
      <h1 class="text-xl mb-2">{{ title }}</h1>
      <div class="flex flex-wrap gap-2">
        <FormContainer :nodes="filteredNodes" :disable-autocomplete="disableAutocomplete" :disabled-fields="disabledFields" :form-tabs="tabs" />
        <slot name="additional-buttons" />
      </div>
    </Card>
  </component>
</template>

<script lang="ts" setup>
import { UiNode } from "@ory/kratos-client";
import { computed, ref } from "vue";
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
  return props.ui.nodes.filter((n) => {
    // group not enabled? bye
    if (!props.includeGroups.includes(n.group)) return false;
    // see if we need to invert the result
    const modeOut = props.fields.length === 0 || props.fieldsAsExcludes;
    // check if field should be included
    const include = props.fields.includes((n.attributes as any).name) || props.fields.includes(n.group + ".*");
    return modeOut ? !include : include;
  });
});

// for debugging
const otherNodesWithGroup = computed<UiNode[]>(() =>
  props.ui.nodes.filter((el) => !filteredNodes.value.includes(el) && props.includeGroups.includes(el.group))
);
const otherNodesWithoutGroup = computed<UiNode[]>(() =>
  props.ui.nodes.filter((el) => !filteredNodes.value.includes(el) && !props.includeGroups.includes(el.group))
);

const empty = computed(() => {
  if (filteredNodes.value.length === 0) {
    return true;
  }
  if (filteredNodes.value.length === 1) {
    return (filteredNodes.value[0].attributes as any).name === "csrf_token";
  }
  return false;
});

const form = ref();

function submit() {
  const submitter = document.querySelector("button[type=submit]");
  if (form.value.requestSubmit && submitter) {
    form.value.requestSubmit(submitter as HTMLElement);
  }
}
</script>

<style scoped></style>
