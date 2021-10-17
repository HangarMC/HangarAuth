<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :title="$t('login.title')" :ui="ui">
            <template #additional-buttons>
                <v-card-actions>
                    <v-btn color="secondary" @click.prevent="$kratos.register()">Register</v-btn>
                    <v-btn color="secondary" @click.prevent="$kratos.reset()">Forgot</v-btn>
                </v-card-actions>
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
