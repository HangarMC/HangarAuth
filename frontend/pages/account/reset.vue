<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('reset.title') }}</v-card-title>
            <v-card-text>
                <p v-text="$t('reset.text1')" />
                <p v-text="$t('reset.text2')" />
                <v-text-field v-model="email" :label="$t('reset.email')" name="email"></v-text-field>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="send">{{ $t('reset.send') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class ResetPage extends Vue {
    title = this.$t('reset.title');

    email = '';

    send() {
        // TODO implement
    }
}
</script>
