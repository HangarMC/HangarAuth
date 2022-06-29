<template>
    <v-app>
        <Header />
        <v-main>
            <v-container>
                <v-row>
                    <nuxt />
                </v-row>
            </v-container>
        </v-main>
        <Footer />
    </v-app>
</template>

<script lang="ts" setup>
import Header from '~/components/Header.vue';
import Footer from '~/components/Footer.vue';

definePageMeta({
  title: "HangarAuth"
});
</script>
