<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('signup.title') }}</v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="username"
                    :label="$t('signup.username')"
                    name="username"
                    persistent-hint
                    :hint="$t('signup.usernameHint')"
                ></v-text-field>
                <v-text-field
                    v-model="password"
                    :label="$t('signup.password')"
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    name="password"
                    persistent-hint
                    :hint="$t('signup.passwordHint')"
                    @click:append="show = !show"
                ></v-text-field>
                <v-text-field v-model="email" :label="$t('signup.email')" name="email" persistent-hint :hint="$t('signup.emailHint')"></v-text-field>
                <v-text-field
                    v-model="fullName"
                    :label="$t('signup.fullName')"
                    name="fullName"
                    persistent-hint
                    :hint="$t('signup.fullNameHint')"
                ></v-text-field>
                <v-text-field
                    v-model="minecraft"
                    :label="$t('signup.minecraft')"
                    name="minecraft"
                    persistent-hint
                    :hint="$t('signup.minecraftHint')"
                ></v-text-field>
                <v-text-field v-model="irc" :label="$t('signup.irc')" name="irc" persistent-hint :hint="$t('signup.ircHint')"></v-text-field>
                <v-text-field v-model="github" :label="$t('signup.github')" name="github" persistent-hint :hint="$t('signup.githubHint')"></v-text-field>
                <v-text-field v-model="discord" :label="$t('signup.discord')" name="discord" persistent-hint :hint="$t('signup.discordHint')"></v-text-field>

                <v-checkbox v-model="tos" :label="$t('signup.tos')"></v-checkbox>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="signup">{{ $t('general.signUp') }}</v-btn>
                <v-btn to="/account/login">{{ $t('general.login') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';

@Component({})
export default class SignUpPage extends Vue {
    title = this.$t('signup.title');

    show = false;

    username = '';
    password = '';
    email = '';
    fullName = '';
    minecraft = '';
    irc = '';
    github = '';
    discord = '';

    tos = false;

    signup() {
        // TODO implement
    }
}
</script>
