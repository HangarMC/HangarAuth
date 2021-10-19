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
import { Component } from 'nuxt-property-decorator';
import { Context } from '@nuxt/types';
import Form from '~/components/form/Form.vue';
import { KratosPage } from '~/components/mixins/Kratos';

@Component({
    components: { Form },
})
export default class LoginPage extends KratosPage {
    head() {
        return {
            title: this.$t('login.title'),
        };
    }

    asyncData({ $kratos, req }: Context) {
        return $kratos.requestUiContainer((flow) =>
            $kratos.client.getSelfServiceLoginFlow(flow, req ? req.headers.cookie : undefined, { withCredentials: true })
        );
    }
}
</script>
