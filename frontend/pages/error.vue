<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ error }}</v-card-title>
            <v-card-text>{{ errorDescription }}</v-card-text>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class Error extends Vue {
    get error() {
        return this.$route.query.error;
    }

    get errorDescription() {
        return this.$route.query.error_description;
    }
}
</script>
