<template>
    <v-form v-if="filteredNodes.length > 0" :method="ui.method" :action="ui.action">
        <v-card class="mt-2">
            <v-card-title v-text="title" />
            <v-card-text>
                <component
                    :is="'form-' + node.attributes.type"
                    v-for="(node, idx) in filteredNodes"
                    :key="idx"
                    :node="node"
                    :disable-autocomplete="disableAutocomplete"
                />
            </v-card-text>
            <v-card-actions>
                <slot name="additional-buttons" />
            </v-card-actions>
        </v-card>
    </v-form>
</template>

<script lang="ts">
import { Component, Prop } from 'nuxt-property-decorator';
import { UiNode } from '@ory/kratos-client';
import { PropType } from 'vue';
import { UiContainer } from '@ory/kratos-client/api';
import FormPassword from '~/components/form/FormPassword.vue';
import FormText from '~/components/form/FormText.vue';
import FormHidden from '~/components/form/FormHidden.vue';
import FormEmail from '~/components/form/FormEmail.vue';
import FormSubmit from '~/components/form/FormSubmit.vue';
import { FormPart } from '~/components/mixins/FormElement';

@Component({
    components: { FormPassword, FormText, FormHidden, FormEmail, FormSubmit },
})
export default class Form extends FormPart {
    @Prop({ type: String, required: true })
    title!: String;

    @Prop({ required: true, type: Object as PropType<UiContainer> })
    ui!: UiContainer;

    @Prop({ type: Array as PropType<string[]>, default: () => [] })
    includeGroups!: string[];

    get filteredNodes(): UiNode[] {
        if (this.includeGroups.length === 0) {
            return this.ui.nodes;
        }
        return this.ui.nodes.filter((n) => this.includeGroups.includes(n.group));
    }

    // get csrfToken(): UiNode {
    //     return (this.ui.nodes.find((n) => this.isInputAttribute(n.attributes) && n.attributes.name === 'csrf_token').attributes as UiNodeInputAttributes).value;
    // }

    // async submit(form: HTMLFormElement) {
    //     try {
    //         const body = this.buildFormData(form);
    //         if (this.method) {
    //             body.method = (this.method[0].attributes as UiNodeInputAttributes).value;
    //         }
    //
    //         console.log('post body', body);
    //         const responseRaw = await fetch(this.ui.action, {
    //             method: this.ui.method,
    //             body: JSON.stringify(body),
    //             headers: {
    //                 'Content-Type': 'application/json',
    //             },
    //             credentials: 'include',
    //         });
    //         const response = await responseRaw.json();
    //         this.$emit('result', response);
    //     } catch (e) {
    //         console.log('error', e);
    //     }
    // }
    //
    // buildFormData(form: any) {
    //     const json: { [key: string]: any } = {
    //         csrf_token: this.csrfToken,
    //     };
    //     new FormData(form).forEach((value, key) => {
    //         json[key] = value;
    //     });
    //     return json;
    // }

    // isInputAttribute(object: any): object is UiNodeInputAttributes {
    //     return 'type' in object;
    // }
    //
    // isImageAttribute(object: any): object is UiNodeImageAttributes {
    //     return 'src' in object;
    // }
}
</script>

<style scoped></style>
