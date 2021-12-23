<template>
    <v-col v-if="ui" md="6" offset-md="3" cols="12" offset="0">
        <UserMessages :ui="ui" />
        <Form :ui="ui" :title="$t('signup.title')" disable-autocomplete />
    </v-col>
    <v-col v-else-if="signupDisabled" md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>Signup is currently disabled!</v-card-title>
            <v-card-text>Come back at a later time please.</v-card-text>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { Context } from '@nuxt/types';
import Form from '~/components/form/Form.vue';
import { KratosPage } from '~/components/mixins/Kratos';
import UserMessages from '~/components/UserMessages.vue';

@Component({
    components: { UserMessages, Form },
})
export default class SignUpPage extends KratosPage {
    get signupDisabled() {
        return process.env.signupDisabled;
    }

    asyncData({ $kratos }: Context) {
        if (process.env.signupDisabled) return;
        return $kratos.requestUiContainer(
            (flow) => $kratos.client.getSelfServiceRegistrationFlow(flow, undefined, { withCredentials: true }),
            $kratos.register,
            $kratos.register
        );
    }
}
</script>
