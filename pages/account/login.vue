<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :ui="ui" :title="$t('login.title')">
            <template #additional-buttons>
                <v-btn @click.prevent="$kratos.register()">Register</v-btn>
                <v-btn @click.prevent="$kratos.reset()">Forgot</v-btn>
            </template>
        </Form>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client/api';
import Form from '~/components/form/Form.vue';

@Component({
    components: { Form },
})
export default class LoginPage extends Vue {
    title = this.$t('login.title');

    ui: UiContainer | null = null;

    async mounted() {
        if (!this.$route.query.flow) {
            this.$kratos.login();
            return;
        }

        try {
            const flowInfo = await this.$kratos.client.getSelfServiceLoginFlow(this.$route.query.flow as string, undefined, { withCredentials: true });
            console.log(flowInfo.data.ui.nodes);
            this.ui = flowInfo.data.ui;
        } catch (e) {
            if (e.response.status === 410) {
                console.log('gone!');
            }
            console.log(e);
        }
    }
}
</script>
