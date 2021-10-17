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
import { Context } from '@nuxt/types';
import Form from '~/components/form/Form.vue';

@Component({
    components: { Form },
})
export default class LoginPage extends Vue {
    title = this.$t('login.title');

    ui: UiContainer | null = null;

    asyncData({ $kratos }: Context) {
        return $kratos.requestUiContainer((flow) => $kratos.client.getSelfServiceLoginFlow(flow, undefined, { withCredentials: true }));
    }
}
</script>
