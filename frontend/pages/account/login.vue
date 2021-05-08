<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('login.title') }}</v-card-title>
            <v-card-text>
                <v-text-field v-model="username" :label="$t('login.username')" name="username"></v-text-field>
                <v-text-field
                    v-model="password"
                    :label="$t('login.password')"
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    name="password"
                    @click:append="show = !show"
                ></v-text-field>
                <NuxtLink to="/account/reset" class="reset-link">{{ $t('login.reset') }}</NuxtLink>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="login">{{ $t('general.login') }}</v-btn>
                <v-btn to="/account/signup">{{ $t('general.signUp') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class LoginPage extends Vue {
    title = this.$t('login.title');

    show = false;

    username = '';
    password = '';

    login() {
        this.$axios
            .post('/api/login/', { username: this.username, password: this.password })
            .then(() => {
                this.$router.push('/');
            })
            .catch((e) => {
                console.log('error', e);
            });
    }
}
</script>

<style lang="scss" scoped>
.reset-link {
    float: right;
    font-style: italic;
}
</style>
