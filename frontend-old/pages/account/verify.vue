<template>
    <v-col v-if="ui" md="6" offset-md="3" cols="12" offset="0">
        <UserMessages :ui="ui" />
        <Form :ui="modifiedUi" :title="$t('verify.title')" />
    </v-col>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { Context } from '@nuxt/types';
import { UiContainer, UiText } from '@ory/kratos-client/api';
import Form from '~/components/form/Form.vue';
import { KratosPage } from '~/components/mixins/Kratos';
import UserMessages from '~/components/UserMessages.vue';

@Component({
    components: { UserMessages, Form },
})
export default class VerifyPage extends KratosPage {
    head() {
        return {
            title: this.$t('verify.title'),
        };
    }

    get modifiedUi(): UiContainer | undefined {
        if (this.ui) {
            const node = this.ui?.nodes.find((n) => n.group === 'link');
            if (node) {
                node.meta.label = {
                    text: 'E-Mail',
                } as UiText;
            }
            return this.ui;
        }
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
