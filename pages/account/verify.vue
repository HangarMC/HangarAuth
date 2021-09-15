<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :ui="ui" :title="$t('verify.title')" @result="result" />
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client/api';
import Form from '~/components/form/Form.vue';

@Component({
    components: { Form },
})
export default class VerifyPage extends Vue {
    title = this.$t('verify.title');

    ui: UiContainer | null = null;

    async mounted() {
        if (!this.$route.query.flow) {
            this.$kratos.verify();
            return;
        }

        try {
            const flowInfo = await this.$kratos.client.getSelfServiceVerificationFlow(this.$route.query.flow as string, undefined, { withCredentials: true });
            console.log(flowInfo.data.ui.nodes);
            this.ui = flowInfo.data.ui;
        } catch (e) {
            if (e.response.status === 410) {
                this.$kratos.verify();
                return;
            }
            console.log(e);
        }
    }

    result(response: any) {
        if (response.ui) {
            this.ui = response.ui;
        } else {
            console.log('wtf', response);
        }
    }
}
</script>
