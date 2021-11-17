<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { UiNodeScriptAttributes } from '@ory/kratos-client';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormScript extends FormElement {
    script: HTMLScriptElement | undefined;

    mounted() {
        const attributes = this.node.attributes as UiNodeScriptAttributes;
        this.script = document.createElement('script');

        this.script.async = true;
        this.script.src = attributes.src;
        this.script.async = attributes.async;
        this.script.crossOrigin = attributes.crossorigin;
        this.script.integrity = attributes.integrity;
        this.script.referrerPolicy = attributes.referrerpolicy;
        this.script.type = attributes.type;

        document.body.appendChild(this.script);
        console.log('script mounted', this.script);
    }

    beforeDestroy() {
        if (this.script) {
            document.body.removeChild(this.script);
        }
    }

    render(h: any) {
        return h();
    }
}
</script>

<style scoped></style>
