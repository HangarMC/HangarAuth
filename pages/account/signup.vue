<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :ui="ui" :title="$t('signup.title')" disable-autocomplete />
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
export default class SignUpPage extends KratosPage {
    asyncData({ $kratos }: Context) {
        return $kratos.requestUiContainer(
            (flow) => $kratos.client.getSelfServiceRegistrationFlow(flow, undefined, { withCredentials: true }),
            $kratos.register,
            $kratos.register
        );
    }
}
</script>
