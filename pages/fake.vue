<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('index.title') }}</v-card-title>
            <v-card-text>
                <v-list>
                    <v-list-item @click="$kratos.register()">Register</v-list-item>
                    <v-list-item @click="$kratos.login()">Login</v-list-item>
                </v-list>
            </v-card-text>
        </v-card>
    </v-col>
</template>

<script>
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class FakePage extends Vue {}
</script>

<style scoped></style>
