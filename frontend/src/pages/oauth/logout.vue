<template>
  <Card>
    Logout!
    <!-- logout UI isn't used right now, logout is accepted automatically by server middleware hydra -->
  </Card>
</template>

<script lang="ts" setup>
import { useHead } from "nuxt/app";
import { useI18n } from "vue-i18n";
import Card from "~/lib/components/design/Card.vue";

const { t } = useI18n();

useHead({
  title: t("logout.title"),
});
</script>
