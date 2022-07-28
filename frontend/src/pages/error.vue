<template>
  <Card>
    <template v-if="errorDetails">
      <h1 class="text-xl mb-4">{{ errorDetails.code }} - {{ errorDetails.message }}</h1>
      <p>
        {{ errorDetails.reason }}
      </p>
    </template>
    <template v-else-if="errorName">
      <h1 class="text-xl mb-4">Technical error: {{ errorName }}</h1>
      <p>
        {{ errorDescription }}
        <br />
        Please report this to the administrators!
      </p>
    </template>
    <template v-else>
      <h1 class="text-xl mb-4">An error occurred</h1>
      <p>Trying to fetch more info...</p>
    </template>
  </Card>
</template>

<script lang="ts" setup>
import Card from "~/lib/components/design/Card.vue";

const { $kratos } = useNuxtApp();
const route = useRoute();

const errorDetails = useState<object>("errorDetails");
const errorName = useState<string>("errorName");
const errorDescription = useState<string>("errorDescription");

const errorId = route.query.id;
if (errorId) {
  errorDetails.value = await $kratos.getErrorDetails(errorId as string);
} else if (route.query.error) {
  errorName.value = route.query.error as string;
  errorDescription.value = route.query.error_description as string;
}

useHead({
  title: "Error",
});
</script>
