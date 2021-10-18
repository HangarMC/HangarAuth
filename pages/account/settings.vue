<template>
    <v-col v-if="ui" md="6" offset-md="3" cols="12" offset="0">
        <v-alert v-for="message in ui.messages" :key="message.id" :type="message.type" v-text="message.text" />
        <v-sheet class="py-2 text-h4 text-center rounded" v-text="$t('settings.title')" />
        <Form :title="$t('settings.userinfo')" disable-autocomplete :ui="ui" :include-groups="['default', 'profile']" />
        <Form :title="$t('settings.password')" disable-autocomplete :ui="ui" :include-groups="['default', 'password']" />
    </v-col>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { Context } from '@nuxt/types';
import { AuthRequired } from '~/middleware/auth';
import Form from '~/components/form/Form.vue';
import { KratosPage } from '~/components/mixins/Kratos';

@Component({
    components: {
        Form,
    },
})
@AuthRequired()
export default class SettingsPage extends KratosPage {
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
