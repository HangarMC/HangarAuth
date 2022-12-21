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
import { computed, useHead, useNuxtApp } from "#imports";

const { t } = useI18n();
const { $kratos } = useNuxtApp();
const authStore = useAuthStore();
const router = useRouter();

useHead({
  title: t("index.title"),
});

const currentUser = computed(() => {
  return authStore.user;
});

if (currentUser.value) {
  // We can add a landing page if/once auth is used for more than just Hangar
  await router.replace("/account/settings");
} else if (authStore.error) {
  // we don't want to cause infinite loops on auth errors
  if (authStore.error.error?.id === "session_aal2_required") {
    // but if its all2 required, we know a 2fa flow was aborted and we can just trigger it again
    await $kratos.aal2();
  } else if (authStore.error.error.reason === "No valid session cookie found.") {
    // no chance of loop, you wanna go to login
    await router.replace("/account/login");
  }
} else {
  // if you are here, you want to go to login actually
  await router.replace("/account/login");
}
</script>
