<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        Logout!
        <!-- logout UI isn't used right now, logout is accepted automatically by server middleware hydra -->
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component
export default class LogoutPage extends Vue {
    head() {
        return {
            title: this.$t('logout.title'),
        };
    }
}
</script>
