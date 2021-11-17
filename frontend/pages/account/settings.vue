<template>
    <v-col v-if="ui" md="6" offset-md="3" cols="12" offset="0">
        <UserMessages :ui="ui" />
        <v-sheet class="py-2 text-h4 text-center rounded" v-text="$t('settings.title')" />
        <Form :title="$t('settings.userinfo')" disable-autocomplete :ui="ui" :include-groups="['default', 'profile']" />
        <Form :title="$t('settings.password')" disable-autocomplete :ui="ui" :include-groups="['default', 'password']" />
        <Form :title="$t('settings.2fa')" disable-autocomplete :ui="ui" :include-groups="['default', 'totp']" />
        <Form :title="$t('settings.2fa-backup')" disable-autocomplete :ui="ui" :include-groups="['default', 'lookup_secret']" />
        <Form :title="$t('settings.webauthn')" disable-autocomplete :ui="ui" :include-groups="['default', 'webauthn']" />
        <v-card class="mt-2">
            <v-card-title v-text="$t('settings.avatar.title')" />
            <v-card-text>
                <v-row>
                    <v-col cols="12" sm="6">
                        <div>current</div>
                        <v-img :src="`${$nuxt.context.env.publicApi}/avatar/${$store.state.user.id}`" width="200" />
                    </v-col>
                    <v-col cols="12" sm="6">
                        <v-form
                            method="POST"
                            :action="`${$nuxt.context.env.publicApi}/avatar/${$store.state.user.id}?flowId=${flowId}`"
                            enctype="multipart/form-data"
                        >
                            <div>new</div>
                            <input type="hidden" name="csrf_token" :value="csrfToken" />
                            <v-file-input
                                v-model="file"
                                name="avatar"
                                prepend-icon="mdi-camera"
                                :placeholder="$t('settings.avatar.inputPlaceholder')"
                                dense
                                single-line
                                hide-details
                                show-size
                                filled
                                :rules="[(v) => v !== null]"
                                accept="image/png,image/jpeg"
                            />
                            <v-btn class="mt-2" block color="primary" type="submit" :disabled="!file" v-text="$t('general.save')" />
                        </v-form>
                    </v-col>
                </v-row>
            </v-card-text>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { Context } from '@nuxt/types';
import { AuthRequired } from '~/middleware/auth';
import Form from '~/components/form/Form.vue';
import { KratosPage } from '~/components/mixins/Kratos';
import UserMessages from '~/components/UserMessages.vue';

@Component({
    components: {
        UserMessages,
        Form,
    },
})
@AuthRequired()
export default class SettingsPage extends KratosPage {
    file: File | null = null;

    head() {
        return {
            title: this.$t('settings.title'),
        };
    }

    asyncData({ $kratos }: Context) {
        return $kratos.requestUiContainer(
            (flow) => $kratos.client.getSelfServiceSettingsFlow(flow, undefined, undefined, { withCredentials: true }),
            $kratos.settings
        );
    }
}
</script>

<style lang="scss" scoped>
.v-card {
    margin-bottom: 10px;
}
</style>
