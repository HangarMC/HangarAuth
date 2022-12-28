<template>
  <div class="flex flex-wrap basis-full form-container">
    <Tabs v-if="filteredTabs.length > 1" v-model="selectedTabValue" :tabs="filteredTabs" :vertical="false">
      <template #catchall>
        <div class="flex flex-wrap gap-2">
          <div v-for="(node, idx) in filteredNodes" :key="idx + selectedTabValue" class="basis-full">
            <component
              :is="'form-' + node.type + ('type' in node.attributes && !node.attributes.type?.includes('/') ? '-' + node.attributes.type : '')"
              :node="node"
              :disable-autocomplete="disableAutocomplete"
              :disabled-field="'name' in node.attributes ? disabledFields.includes(node.attributes.name) : false"
            />
          </div>
        </div>
      </template>
    </Tabs>
    <div v-else class="flex flex-wrap gap-2">
      <div v-for="(node, idx) in filteredNodes" :key="idx + selectedTabValue" class="basis-full">
        <component
          :is="'form-' + node.type + ('type' in node.attributes && !node.attributes.type?.includes('/') ? '-' + node.attributes.type : '')"
          :node="node"
          :disable-autocomplete="disableAutocomplete"
          :disabled-field="'name' in node.attributes ? disabledFields.includes(node.attributes.name) : false"
        />
      </div>
    </div>
    <div v-for="(node, idx) in noTab" :key="idx" class="basis-full mb-2">
      <component
        :is="'form-' + node.type + ('type' in node.attributes && !node.attributes.type?.includes('/') ? '-' + node.attributes.type : '')"
        :node="node"
        :disable-autocomplete="disableAutocomplete"
        :disabled-field="'name' in node.attributes ? disabledFields.includes(node.attributes.name) : false"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { UiNode } from "@ory/kratos-client";
import { computed, ref } from "vue";
import Tabs, { Tab } from "~/lib/components/design/Tabs.vue";

export interface FormTab extends Tab {
  value: string;
  groups: string[];
  header: string;
}

const props = defineProps<{
  nodes: UiNode[];
  disableAutocomplete: boolean;
  disabledFields: string[];
  formTabs: FormTab[];
}>();

const selectedTabValue = ref();

const selectedTab = computed(() => {
  return props.formTabs?.find((t) => t.value === selectedTabValue.value);
});

const noTab = computed(() => {
  return props.nodes.filter((n) => {
    for (const tab of props.formTabs) {
      if (matches(n, tab)) {
        return false;
      }
    }
    return true;
  });
});

function matches(node: UiNode, tab: FormTab, includeCsrf = true) {
  if (!tab) return false;
  const isCsrf = "name" in node.attributes && node.attributes.name === "csrf_token";
  if (isCsrf) {
    return includeCsrf;
  }
  return tab.groups.includes(node.group);
}

const filteredNodes = computed(() => {
  const selected = selectedTab.value;
  return selected ? props.nodes.filter((n) => matches(n, selected)) : [];
});

const filteredTabs = computed(() => {
  return props.formTabs.filter((tab) =>
    props.nodes.find((n) => {
      return matches(n, tab, false);
    })
  );
});

// select first tab
if (props.formTabs && props.formTabs.length > 0) {
  selectedTabValue.value = filteredTabs.value[0].value;
}
</script>

<style>
.form-container div:has(> input[type="hidden"]) {
  display: none;
}
</style>
