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
}
</script>

<style scoped></style>
