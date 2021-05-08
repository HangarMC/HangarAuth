<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('logout.title') }}</v-card-title>
            <v-card-text><p v-text="$t('logout.text')" /></v-card-text>
            <v-card-actions>
                <v-btn color="primary" to="/account/login">{{ $t('general.login') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class LoginPage extends Vue {
    title = this.$t('logout.title');
}
</script>
