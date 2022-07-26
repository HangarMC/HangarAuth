<template>
  <div class="background-body text-[#262626] dark:text-[#E0E6f0]">
    <Header />
    <Container class="mb-24 md:mb-16">
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
