<template>
    <v-form :action="ui.action" :method="ui.method">
        <v-card>
            <v-card-title>{{ title }}</v-card-title>
            <v-card-text>
                <v-alert v-for="message in ui.messages" :key="message.id" :type="message.type" v-text="message.text" />
                <component :is="'form-' + node.attributes.type" v-for="(node, idx) in elements" :key="idx" :node="node" />
            </v-card-text>
            <v-card-actions>
                <component :is="'form-' + node.attributes.type" v-for="(node, idx) in buttons" :key="idx" :node="node" />
            </v-card-actions>
        </v-card>
    </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client';
import { PropType } from 'vue';
import { UiNodeInputAttributes } from '@ory/kratos-client/api';
import FormPassword from '~/components/form/FormPassword.vue';
import FormText from '~/components/form/FormText.vue';
import FormHidden from '~/components/form/FormHidden.vue';
import FormEmail from '~/components/form/FormEmail.vue';
import FormSubmit from '~/components/form/FormSubmit.vue';

@Component({
    components: { FormPassword, FormText, FormHidden, FormEmail, FormSubmit },
})
export default class Form extends Vue {
    @Prop({ type: Object as PropType<UiContainer>, required: true })
    ui!: UiContainer;

    @Prop({ type: String, required: true })
    title!: String;

    get buttons() {
        return this.ui.nodes.filter((n) => (n.attributes as UiNodeInputAttributes).type === 'submit');
    }

    get elements() {
        return this.ui.nodes.filter((n) => (n.attributes as UiNodeInputAttributes).type !== 'submit');
    }
}
</script>

<style scoped></style>
