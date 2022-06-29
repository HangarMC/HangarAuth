<template>
  <Card v-if="currentUser">
    <h1>{{ t("index.title") }}</h1>

    <p>{{ t("index.text", [currentUserName]) }}</p>

    <Alert v-if="!verified" type="warning"> Your account is not verified yet, <a @click="$kratos.verify()">click here</a> to change that! </Alert>

    <Alert v-if="verified && !aal2" type="warning">
      You haven't set up 2fa yet, go to <NuxtLink to="/account/settings">the settings</NuxtLink> to change that!
    </Alert>

    <ul>
      <li v-for="(item, idx) in actions" :key="idx">
        <NuxtLink :href="item.href" :to="item.to">
          {{ item.title }}
          <!-- todo icon item.icon -->
        </NuxtLink>
      </li>
    </ul>

    <Button button-type="primary" @click="$kratos.logout()">{{ t("general.logout") }}</Button>
  </Card>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useHead, useNuxtApp } from "nuxt/app";
import Card from "~/lib/components/design/Card.vue";
import Alert from "~/lib/components/design/Alert.vue";
import Button from "~/lib/components/design/Button.vue";
import { useAuthStore } from "~/store/useAuthStore";

// TODO auth required
const authStore = useAuthStore();
const { t } = useI18n();
const { $kratos } = useNuxtApp();

useHead({
  title: t("index.title"),
});

const verified = computed(() => {
  const user = authStore.user;
  if (!user || !user.verifiable_addresses) {
    return false;
  }
  for (const verifiableAddress of user.verifiable_addresses) {
    if (verifiableAddress.verified) {
      return true;
    }
  }
  return false;
});

const aal2 = computed(() => {
  if (verified.value) {
    return authStore.aal?.aal === "aal2";
  }
  return false;
});

const actions = computed(() => {
  const a = [];
  a.push(
    { title: t("index.download"), icon: "mdi-download", href: "https://papermc.io/downloads" },
    { title: t("index.forums"), icon: "mdi-message-reply", href: "https://papermc.io/forums/" },
    { title: t("index.plugins"), icon: "mdi-power-plug", href: process.env.hangarHost },
    { title: t("index.upload"), icon: "mdi-upload", href: "https://hangar.benndorf.dev/new" },
    { title: t("index.manage"), icon: "mdi-cog", to: "/account/settings" }
  );
  return a;
});

const currentUser = computed(() => {
  return authStore.user;
});

const currentUserName = computed(() => {
  return authStore.user?.traits.username;
});

await $kratos.loadUser();
</script>
