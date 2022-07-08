<template>
  <Card v-if="currentUser">
    <h1 class="text-xl mb-4">{{ t("index.title") }}</h1>

    <p>{{ t("index.text", [currentUserName]) }}</p>

    <Alert v-if="!verified" type="warning"> Your account is not verified yet, <a @click="$kratos.verify()">click here</a> to change that! </Alert>

    <Alert v-if="verified && !aal2" type="warning">
      You haven't set up 2fa yet, go to <NuxtLink to="/account/settings">the settings</NuxtLink> to change that!
    </Alert>

    <ul>
      <li v-for="(item, idx) in actions" :key="idx">
        <Link :href="item.href" :to="item.to" class="flex">
          {{ item.title }}
          <component :is="item.icon" class="ml-2" />
        </Link>
      </li>
    </ul>

    <Button button-type="primary" size="medium" class="mt-2" @click="$kratos.logout()">{{ t("general.logout") }}</Button>
  </Card>
  <Card v-else>
    <h1 class="text-xl mb-4">Logged out!</h1>
    <Button button-type="primary" size="medium" class="mt-2" @click="$kratos.login()">{{ t("general.login") }}</Button>
  </Card>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { useHead, useNuxtApp, useRoute, useRuntimeConfig } from "nuxt/app";
import Card from "~/lib/components/design/Card.vue";
import Alert from "~/lib/components/design/Alert.vue";
import Button from "~/lib/components/design/Button.vue";
import { useAuthStore } from "~/store/useAuthStore";
import Link from "~/lib/components/design/Link.vue";
import IconMdiDownload from "~icons/mdi/download";
import IconMdiMessageReply from "~icons/mdi/message-reply";
import IconMdiPowerPlug from "~icons/mdi/power-plug";
import IconMdiUpload from "~icons/mdi/upload";
import IconMdiCog from "~icons/mdi/cog";

// TODO auth required
const authStore = useAuthStore();
const { t } = useI18n();
const { $kratos } = useNuxtApp();
const config = useRuntimeConfig();
const route = useRoute();

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
    { title: t("index.download"), icon: IconMdiDownload, href: "https://papermc.io/downloads" },
    { title: t("index.forums"), icon: IconMdiMessageReply, href: "https://papermc.io/forums/" },
    { title: t("index.plugins"), icon: IconMdiPowerPlug, href: config.public.hangarHost },
    { title: t("index.upload"), icon: IconMdiUpload, href: "https://hangar.benndorf.dev/new" },
    { title: t("index.manage"), icon: IconMdiCog, to: "/account/settings" }
  );
  return a;
});

const currentUser = computed(() => {
  return authStore.user;
});

const currentUserName = computed(() => {
  return authStore.user?.traits.username;
});

if (route.query.loggedOut === undefined) {
  await $kratos.loadUser();
}
</script>
