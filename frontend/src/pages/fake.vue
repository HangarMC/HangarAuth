<template>
  <Card>
    <a :href="buildUrl(null)"> No Prompt, login if necessary</a><br />
    <a :href="buildUrl('none')"> Prompt None, assume we got login, fail otherwise </a><br />
    <a :href="buildUrl('login')"> Prompt Login, force login, even if already logged in </a><br />
    <a :href="buildUrl('consent')"> Prompt Consent, force consent, even if consent was given with 'remember' </a><br />
  </Card>
</template>

<script lang="ts" setup>
import Card from "~/lib/components/design/Card.vue";
import { useHead } from "nuxt/app";

useHead({
  title: "Fake",
});

function buildUrl(prompt: string) {
  const url = new URL("/oauth2/auth", "http://localhost:4444");
  url.searchParams.set("client_id", "my-client");
  url.searchParams.set("scope", "openid");
  url.searchParams.set("response_type", "code");
  url.searchParams.set("redirect_url", "http://localhost:3001/redirect");
  url.searchParams.set("state", "wowthisissocool");
  if (prompt) {
    url.searchParams.set("prompt", prompt);
  }
  return url.toString();
}
</script>
