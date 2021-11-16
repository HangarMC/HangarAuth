<template>
    <img :id="node.attributes.id" :src="node.attributes.src" :height="node.attributes.height" :width="node.attributes.width" :alt="node.meta.label.text" />
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { UiNodeInputAttributes } from '@ory/kratos-client/api';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormImg extends FormElement {
    get autocomplete() {
        switch ((this.node.attributes as UiNodeInputAttributes).name) {
            case 'password_identifier':
            case 'traits.username':
                return 'username';
            case 'traits.name.first':
                return 'given-name';
            case 'traits.name.last':
                return 'family-name';
            case 'traits.language':
                return 'language';
        }
    }
}
</script>

<style scoped></style>
