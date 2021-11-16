<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card v-if="errorDetails">
            <v-card-title>{{ errorDetails.code }} - {{ errorDetails.message }}</v-card-title>
            <v-card-text>
                {{ errorDetails.reason }}
            </v-card-text>
        </v-card>
        <v-card v-else>
            <v-card-title>An error occurred</v-card-title>
            <v-card-text>Trying to fetch more info...</v-card-text>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class Error extends Vue {
    errorDetails: object = {};

    async created() {
        const errorId = this.$route.query.id;
        if (errorId) {
            this.errorDetails = await this.$kratos.getErrorDetails(errorId as string);
        }
    }
}
</script>
