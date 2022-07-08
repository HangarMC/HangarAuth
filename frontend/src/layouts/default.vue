<template>
  <div>
    <Header />
    <Container class="mb-16">
      <slot />
    </Container>
    <Footer />
  </div>
</template>

<script lang="ts" setup>
import Header from "~/components/Header.vue";
import Footer from "~/components/Footer.vue";
import Container from "~/lib/components/design/Container.vue";

definePageMeta({
  title: "HangarAuth",
});
</script>
