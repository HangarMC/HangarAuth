<template>
  <Card v-if="error">
    <h5>Error processing consent request</h5>
    {{ error }}
  </Card>
  <form v-else action="/oauth/consent" method="POST">
    <input name="challenge" type="hidden" :value="challenge" />
    <input name="_csrf" type="hidden" :value="csrfToken" />
    <Card>
      <h5>Consent Required!</h5>
      <p>
        <template v-if="user">Hi {{ user }}, application</template>
        <template v-else>Application</template>
        <strong>{{ clientName }}</strong> wants access resources on your behalf and to:
      </p>

      <div class="mt-2 bt-2">Scopes:</div>
      <div v-for="scope in requestedScope" :key="scope" class="mb-2">
        <!-- todo make sure these checkboxes still work -->
        <InputCheckbox :id="scope" class="inline-block mr-1" :model-value="scope" name="grant_scope" />
        <div class="inline-block">{{ scope }}</div>
      </div>

      <ul>
        <li v-if="policyUrl">
          <a :href="policyUrl">Read the Privacy Policy</a>
        </li>
        <li v-if="tosUri">
          <a :href="tosUri">Terms of Service</a>
        </li>
      </ul>

      <div>
        <InputCheckbox id="remember" :model-value="1" name="remember" />
        <div style="display: inline-block; margin-right: 3px">Do not ask me again</div>
      </div>

      <Button id="accept" button-type="primary" type="submit" name="submit" value="Allow access">Allow access</Button>
      <Button id="deny" button-type="red" type="submit" name="submit" value="Deny access">Deny access</Button>
    </Card>
  </form>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import Button from "~/lib/components/design/Button.vue";
import InputCheckbox from "~/lib/components/ui/InputCheckbox.vue";
import Card from "~/lib/components/design/Card.vue";
import { useAuthStore } from "~/store/useAuthStore";

// TODO replace this shit
// todo what did the passheader middleware do?
const store = useAuthStore();
const error = computed(() => (store.hydraData && store.hydraData.length === 1 ? store.hydraData[0] : null));
const challenge = computed(() => store.hydraData[0]);
const csrfToken = computed(() => store.hydraData[1]);
const user = computed(() => store.hydraData[2]);
const clientName = computed(() => store.hydraData[3]);
const requestedScope = computed(() => store.hydraData[4]?.split(","));
const policyUrl = computed(() => store.hydraData[5]);
const tosUri = computed(() => store.hydraData[6]);
</script>
