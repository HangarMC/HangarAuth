<template>
  <Card v-if="!currentUser">
    <h1 class="text-xl mb-4">You are currently not logged in.</h1>
    <Button button-type="primary" size="medium" @click="$kratos.login()">{{ t("general.login") }}</Button>
  </Card>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";
import Card from "~/lib/components/design/Card.vue";
import Button from "~/lib/components/design/Button.vue";
import { useAuthStore } from "~/store/useAuthStore";

const { t } = useI18n();
const { $kratos } = useNuxtApp();
const route = useRoute();
const authStore = useAuthStore();
const router = useRouter();

useHead({
  title: t("index.title"),
});

const currentUser = computed(() => {
  return authStore.user;
});

if (route.query.loggedOut === undefined) {
  await $kratos.loadUser();
}

if (currentUser) {
  // We can add a landing page if/once auth is used for more than just Hangar
  router.replace("/account/settings");
}
</script>
