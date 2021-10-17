<template>
    <v-col v-if="ui" md="6" offset-md="3" cols="12" offset="0">
        <v-alert v-for="message in ui.messages" :key="message.id" :type="message.type" v-text="message.text" />
        <v-sheet class="py-2 text-h4 text-center rounded" v-text="$t('settings.title')" />
        <Form :title="$t('settings.userinfo')" disable-autocomplete :ui="ui" :include-groups="['default', 'profile']" />
        <Form :title="$t('settings.password')" disable-autocomplete :ui="ui" :include-groups="['default', 'password']" />
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client/api';
import { AuthRequired } from '~/middleware/auth';
import Form from '~/components/form/Form.vue';

@Component({
    components: {
        Form,
    },
})
@AuthRequired()
export default class SettingsPage extends Vue {
    title = this.$t('settings.title');

    ui: UiContainer | null = null;

    async mounted() {
        const flow = this.$route.query.flow;
        if (!flow || Array.isArray(flow)) {
            this.$kratos.settings();
            return;
        }

        try {
            const flowInfo = await this.$kratos.client.getSelfServiceSettingsFlow(flow, undefined, undefined, { withCredentials: true });
            console.log(flowInfo.data.ui);
            this.ui = flowInfo.data.ui;
        } catch (e) {
            if (e.response.status === 410) {
                this.$kratos.login();
                return;
            }
            console.log(e);
        }
    }
}
</script>

<style lang="scss" scoped>
.v-card {
    margin-bottom: 10px;
}
</style>
