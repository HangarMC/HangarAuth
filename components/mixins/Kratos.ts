import { Component, Vue } from 'nuxt-property-decorator';
import { UiContainer } from '@ory/kratos-client/api';

@Component
export class KratosPage extends Vue {
    ui: UiContainer | null = null;
}
