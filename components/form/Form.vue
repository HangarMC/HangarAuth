<template>
    <v-form @submit.prevent="submit">
        <v-card>
            <v-card-title>{{ title }}</v-card-title>
            <v-card-text>
                <v-alert v-for="message in ui.messages" :key="message.id" :type="message.type" v-text="message.text" />
                <component :is="'form-' + node.attributes.type" v-for="(node, idx) in elements" :key="idx" :node="node" />
                <slot name="additional-fields"></slot>
            </v-card-text>
            <v-card-actions>
                <component :is="'form-' + node.attributes.type" v-for="(node, idx) in buttons" :key="idx" :node="node" />
                <slot name="additional-buttons"></slot>
            </v-card-actions>
        </v-card>
    </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'nuxt-property-decorator';
import { UiContainer, UiNode } from '@ory/kratos-client';
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

    get buttons(): Array<UiNode> {
        return this.ui.nodes.filter((n) => (n.attributes as UiNodeInputAttributes).type === 'submit');
    }

    get elements(): Array<UiNode> {
        return this.ui.nodes.filter((n) => (n.attributes as UiNodeInputAttributes).type !== 'submit');
    }

    get method(): Array<UiNode> {
        return this.ui.nodes.filter((n) => (n.attributes as UiNodeInputAttributes).name === 'method');
    }

    async submit() {
        try {
            const body = this.buildFormData(this.$el);
            if (this.method) {
                body.method = (this.method[0].attributes as UiNodeInputAttributes).value;
            }

            console.log('post body', body);
            const responseRaw = await fetch(this.ui.action, {
                method: this.ui.method,
                body: JSON.stringify(body),
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });
            const response = await responseRaw.json();
            this.$emit('result', response);
        } catch (e) {
            console.log('error', e);
        }
    }

    buildFormData(form: any) {
        const json: any = {};
        new FormData(form).forEach((value, key) => {
            json[key] = value;
        });
        return json;
    }
}
</script>

<style scoped></style>
