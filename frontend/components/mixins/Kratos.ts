import { Component, Prop, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client/api';
import { PropType } from 'vue';

@Component
export class KratosPage extends Vue {
    ui: UiContainer | null = null;
}

@Component
export class KratosUiProp extends Vue {
    @Prop({ required: true, type: Object as PropType<UiContainer> })
    ui!: UiContainer;
}
