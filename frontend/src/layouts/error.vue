<template>
  <Card max-width="400" class="mx-auto">
    <h1>{{ error.statusCode }}</h1>
    <p>{{ text }}</p>
    <!-- nuxt button? -->
    <Button nuxt to="/" color="secondary">
      <IconMdiHome />
      {{ t("general.home") }}
    </Button>
  </Card>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { computed } from "vue";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import { NuxtError } from "@nuxt/types";

const props = defineProps<{
  error: NuxtError;
}>();

const { t } = useI18n();

const text = computed(() => {
  switch (props.error.statusCode) {
    case 404:
      return t("error.404");
    case 401:
      return t("error.401");
    case 403:
      return t("error.403");
    default:
      return t("error.unknown");
  }
});

const head = computed(() => {
  let title = t("error.unknown");
  switch (props.error.statusCode) {
    case 404:
      title = t("error.404");
      break;
    case 401:
      title = props.error.message!;
      break;
  }
  return {
    title,
  };
});
</script>
