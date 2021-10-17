import { Component, Prop, Vue } from 'nuxt-property-decorator';
import { UiNode } from '@ory/kratos-client';
import { PropType } from 'vue';

@Component
export class FormPart extends Vue {
    @Prop({ type: Boolean, default: false })
    disableAutocomplete!: boolean;
}

@Component
export class FormElement extends FormPart {
    @Prop({ type: Object as PropType<UiNode>, required: true })
    node!: UiNode;

    get errorMessages(): string[] {
        return this.node.messages.filter((m) => m.type === 'error').map((m) => m.text);
    }

    get messages(): string[] {
        return this.node.messages.filter((m) => m.type !== 'error').map((m) => m.text);
    }
}
