import { Component, Prop, Vue } from 'nuxt-property-decorator';
import { UiContainer, UiNodeInputAttributes } from '@ory/kratos-client/api';
import { PropType } from 'vue';

@Component
export class KratosPage extends Vue {
    ui: UiContainer | null = null;
    flowId: string | null = null;

    get csrfToken() {
        if (!this.ui) {
            throw new Error('Must have UI to get a csrfToken');
        }
        const node = this.ui.nodes.find((n) => 'name' in n.attributes && n.attributes.name === 'csrf_token');
        if (!node) {
            throw new Error('No csrf token found');
        }
        return (node.attributes as UiNodeInputAttributes).value;
    }
}

@Component
export class KratosUiProp extends Vue {
    @Prop({ required: true, type: Object as PropType<UiContainer> })
    ui!: UiContainer;
}
