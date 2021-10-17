<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <Form v-if="ui" :ui="ui" :title="$t('signup.title')" disable-autocomplete />
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
export default class SignUpPage extends Vue {
    title = this.$t('signup.title');

    ui: UiContainer | null = null;

    asyncData({ $kratos }: Context) {
        return $kratos.requestUiContainer(
            (flow) => $kratos.client.getSelfServiceRegistrationFlow(flow, undefined, { withCredentials: true }),
            $kratos.register,
            $kratos.register
        );
    }
}
</script>
