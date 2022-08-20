<template>
  <div class="flex flex-wrap basis-full">
    <Tabs v-if="tabs" v-model="selectedTabValue" :tabs="filteredTabs" :vertical="false">
      <template #catchall>
        <div class="flex flex-wrap gap-2">
          <div v-for="(node, idx) in filteredNodes" :key="idx + selectedTabValue" class="basis-full">
            <component
              :is="'form-' + node.type + (node.attributes.type && !node.attributes.type.includes('/') ? '-' + node.attributes.type : '')"
              :node="node"
              :disable-autocomplete="disableAutocomplete"
              :disabled-field="disabledFields.includes(node.attributes.name)"
            />
          </div>
        </div>
      </template>
    </Tabs>
    <div v-for="(node, idx) in noTab" :key="idx" class="basis-full mb-2">
      <component
        :is="'form-' + node.type + (node.attributes.type && !node.attributes.type.includes('/') ? '-' + node.attributes.type : '')"
        :node="node"
        :disable-autocomplete="disableAutocomplete"
        :disabled-field="disabledFields.includes(node.attributes.name)"
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
}

const props = defineProps<{
  nodes: UiNode[];
  disableAutocomplete: boolean;
  disabledFields: string[];
  tabs: FormTab[];
}>();

const selectedTabValue = ref();

const selectedTab = computed(() => {
  return props.tabs.find((t) => t.value === selectedTabValue.value);
});

const noTab = computed(() => {
  return props.nodes.filter((n) => {
    for (let tab of props.tabs) {
      if (matches(n, tab)) {
        return false;
      }
    }
    return true;
  });
});

function matches(node: UiNode, tab: FormTab, includeCsrf = true) {
  if (!tab) return false;
  const isCsrf = node.attributes.name === "csrf_token";
  if (isCsrf) {
    return includeCsrf;
  }
  return tab.groups.includes(node.group);
}

const filteredNodes = computed(() => {
  return props.nodes.filter((n) => matches(n, selectedTab.value));
});

const filteredTabs = computed(() => {
  return props.tabs.filter((tab) =>
    props.nodes.find((n) => {
      return matches(n, tab, false);
    })
  );
});

// select first tab
if (props.tabs && props.tabs.length > 0) {
  selectedTabValue.value = filteredTabs.value[0].value;
}
</script>
