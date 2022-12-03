<template>
  <Card v-if="error">
    <h1 class="text-xl">Error processing consent request</h1>
    {{ error }}
  </Card>
  <Card v-else-if="!consentData">
    <h1 class="text-xl">Error processing consent request</h1>
    No consent data
  </Card>
  <Card v-else-if="consentData.redirectTo">
    <h1 class="text-xl">Redirecting...</h1>
    <Link :href="consentData.redirectTo">Click here if nothing happens</Link>
  </Card>
  <form v-else action="/oauth/handleConsent" method="POST">
    <input name="challenge" type="hidden" :value="consentData.challenge" />
    <input name="_csrf" type="hidden" :value="consentData.csrfToken" />
    <Card>
      <h1 class="text-xl">Consent Required!</h1>
      <p>
        <template v-if="consentData.username">Hi {{ consentData.username }}, application</template>
        <template v-else>Application</template>
        <strong>{{ " " + consentData.clientName }}</strong> wants access resources on your behalf and to:
      </p>

      <div class="mt-2 bt-2">Scopes:</div>
      <div v-for="scope in consentData.requestScope" :key="scope" class="mb-2">
        <InputCheckbox :id="scope" class="inline-block mr-1" :value="scope" name="grant_scope" :label="scope" />
      </div>

      <ul class="mt-2">
        <li v-if="consentData.policyUri">
          <a :href="consentData.policyUrl">Read the Privacy Policy</a>
        </li>
        <li v-if="consentData.tosUri">
          <a :href="consentData.tosUri">Terms of Service</a>
        </li>
      </ul>

      <div class="mt-2">
        <InputCheckbox id="remember" value="1" name="remember" label="Do not ask me again" />
      </div>

      <Button id="accept" button-type="primary" type="submit" name="submit" value="Allow access">Allow access</Button>
      <Button id="deny" class="ml-2" button-type="red" type="submit" name="submit" value="Deny access">Deny access </Button>
    </Card>
  </form>
</template>

<script lang="ts" setup>
import { sendRedirect } from "h3";
import Button from "~/lib/components/design/Button.vue";
import InputCheckbox from "~/lib/components/ui/InputCheckbox.vue";
import Card from "~/lib/components/design/Card.vue";
import Link from "~/lib/components/design/Link.vue";
import { useFetch, useNuxtApp, useRoute, useRuntimeConfig } from "#imports";

interface ConsentData {
  redirectTo: string;
  challenge: string;
  csrfToken: string;
  username: string;
  clientName: string;
  requestScope: string[];
  policyUri: string;
  tosUri: string;
}

const route = useRoute();
const nuxtApp = useNuxtApp();
const config = useRuntimeConfig();
const { data: consentData, error } = await useFetch<ConsentData>((process.server ? config.backendHost : config.public.publicHost) + "/oauth/handleConsent", {
  params: { consent_challenge: route.query.consent_challenge },
});

if (consentData?.value?.redirectTo) {
  if (process.server) {
    nuxtApp.callHook("app:redirected").then(() => sendRedirect(nuxtApp.ssrContext!.event, consentData.value.redirectTo, 301));
  } else {
    location.href = consentData.value.redirectTo;
  }
}
</script>
