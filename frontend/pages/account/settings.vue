<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <!-- todo continue here -->
        <v-card>
            <v-card-title>{{ $t('settings.userinfo') }}</v-card-title>
            <v-card-text> </v-card-text>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.avatar') }}</v-card-title>
            <v-card-text> </v-card-text>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.profile') }}</v-card-title>
            <v-card-text> </v-card-text>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.userinfo') }}</v-card-title>
            <v-card-text> </v-card-text>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class SettingsPage extends Vue {
    title = this.$t('settings.title');
}
</script>

<style lang="scss" scoped>
.v-card {
    margin-bottom: 10px;
}
</style>
