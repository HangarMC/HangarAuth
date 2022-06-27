<template>
    <v-form v-if="filteredNodes.length > 0" :method="ui.method" :action="ui.action">
        <v-card class="mt-2">
            <v-card-title v-text="title" />
            <v-card-text>
                <component
                    :is="'form-' + node.type + (node.attributes.type && !node.attributes.type.includes('/') ? '-' + node.attributes.type : '')"
                    v-for="(node, idx) in filteredNodes"
                    :key="idx"
                    :node="node"
                    :disable-autocomplete="disableAutocomplete"
                    :disabled-field="disabledFields.includes(node.attributes.name)"
                />
            </v-card-text>
            <v-card-actions>
                <slot name="additional-buttons" />
            </v-card-actions>
        </v-card>
    </v-form>
</template>

<script lang="ts">
import { Component, mixins, Prop } from 'nuxt-property-decorator';
import { UiNode } from '@ory/kratos-client';
import { PropType } from 'vue';
import { TranslateResult } from 'vue-i18n';
import FormInputPassword from '~/components/form/FormInputPassword.vue';
import FormInputText from '~/components/form/FormInputText.vue';
import FormInputHidden from '~/components/form/FormInputHidden.vue';
import FormInputEmail from '~/components/form/FormInputEmail.vue';
import FormInputSubmit from '~/components/form/FormInputSubmit.vue';
import FormInputButton from '~/components/form/FormInputButton.vue';
import FormImg from '~/components/form/FormImg.vue';
import FormText from '~/components/form/FormText.vue';
import FormScript from '~/components/form/FormScript.vue';
import { FormPart } from '~/components/mixins/FormElement';
import { KratosUiProp } from '~/components/mixins/Kratos';

@Component({
    components: { FormInputPassword, FormInputText, FormInputHidden, FormInputEmail, FormInputSubmit, FormImg, FormText, FormInputButton, FormScript },
})
export default class Form extends mixins(FormPart, KratosUiProp) {
    @Prop({ type: String as PropType<TranslateResult>, required: true })
    title!: string;

    @Prop({ type: Array as PropType<string[]>, default: () => [] })
    includeGroups!: string[];

    @Prop({ type: Array as PropType<string[]>, default: () => [] })
    disabledFields!: string[];

    get filteredNodes(): UiNode[] {
        if (this.includeGroups.length === 0) {
            return this.ui.nodes;
        }
        return this.ui.nodes.filter((n) => this.includeGroups.includes(n.group));
    }
}
</script>

<style scoped></style>
