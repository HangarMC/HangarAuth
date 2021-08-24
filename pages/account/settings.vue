<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('settings.userinfo') }}</v-card-title>
            <v-card-text>
                <v-simple-table>
                    <tbody>
                        <tr>
                            <td>{{ $t('settings.username') }}</td>
                            <td>{{ user.name }}</td>
                        </tr>
                        <tr>
                            <td>{{ $t('settings.email') }}</td>
                            <td>
                                {{ user.email }}
                                <v-icon @click="$router.push('/account/change-email')">mdi-pen</v-icon>
                            </td>
                        </tr>
                        <tr>
                            <td>{{ $t('settings.created') }}</td>
                            <td>{{ user.createdAt }}</td>
                        </tr>
                        <tr>
                            <td>{{ $t('settings.2fa') }}</td>
                            <td>
                                Disabled
                                <v-btn color="primary" to="/2fa">{{ $t('settings.manage2fa') }}</v-btn>
                            </td>
                        </tr>
                    </tbody>
                </v-simple-table>
            </v-card-text>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.avatar') }}</v-card-title>
            <v-card-text> </v-card-text>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.profile') }}</v-card-title>
            <v-card-text>
                <v-text-field v-model="profile.fullName" :label="$t('settings.fullName')" name="fullName"></v-text-field>
                <v-text-field v-model="profile.minecraft" :label="$t('settings.minecraft')" name="minecraft"></v-text-field>
                <v-text-field v-model="profile.irc" :label="$t('settings.irc')" name="irc"></v-text-field>
                <v-text-field v-model="profile.github" :label="$t('settings.github')" name="github"></v-text-field>
                <v-text-field v-model="profile.discord" :label="$t('settings.discord')" name="discord"></v-text-field>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="changePassword">{{ $t('settings.saveProfile') }}</v-btn>
            </v-card-actions>
        </v-card>

        <v-card>
            <v-card-title>{{ $t('settings.password') }}</v-card-title>
            <v-card-text>
                <v-text-field
                    v-model="newPassword"
                    :label="$t('settings.newPassword')"
                    :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                    :type="show ? 'text' : 'password'"
                    name="newPassword"
                    @click:append="show = !show"
                ></v-text-field>
                <v-text-field v-model="oldPassword" :label="$t('settings.oldPassword')" type="password" name="oldPassword"></v-text-field>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="changePassword">{{ $t('settings.changePassword') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { AuthRequired } from '~/middleware/auth';

@Component({})
@AuthRequired()
export default class SettingsPage extends Vue {
    title = this.$t('settings.title');

    user = {
        name: 'Test',
        email: 'admin@minidigger.me',
        createdAt: '',
    };

    profile = {
        fullName: '',
        minecraft: '',
        irc: '',
        github: '',
        discord: '',
    };

    show = false;
    newPassword = '';
    oldPassword = '';

    changePassword() {}

    saveProfile() {}
}
</script>

<style lang="scss" scoped>
.v-card {
    margin-bottom: 10px;
}
</style>
