<template>
  <div class="background-body text-[#262626] dark:text-[#E0E6f0]">
    <Card max-width="400" class="mx-auto">
      <h1>{{ error.statusCode }}</h1>
      <p>{{ text }}</p>
      <!-- todo nuxt button? -->
      <Button nuxt to="/" color="secondary">
        <IconMdiHome />
        {{ t("general.home") }}
      </Button>
    </Card>
  </div>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { type NuxtError } from "nuxt/app";
import { computed } from "#imports";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";

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
</script>
