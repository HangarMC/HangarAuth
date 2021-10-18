<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :ui="ui" :title="$t('verify.title')" />
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
export default class VerifyPage extends KratosPage {
    head() {
        return {
            title: this.$t('verify.title'),
        };
    }

    asyncData({ $kratos }: Context) {
        return $kratos.requestUiContainer(
            (flow) => $kratos.client.getSelfServiceVerificationFlow(flow, undefined, { withCredentials: true }),
            $kratos.verify,
            $kratos.verify
        );
    }
}
</script>
