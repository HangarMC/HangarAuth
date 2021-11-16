<template>
    <v-text-field
        :disabled="node.attributes.disabled"
        :name="node.attributes.name"
        :required="node.attributes.required"
        :label="node.meta.label ? node.meta.label.text : null"
        :messages="messages"
        :error-messages="errorMessages"
        type="text"
        persistent-hint
        :autocomplete="autocomplete"
        :value="node.attributes.value"
    />
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { UiNodeInputAttributes } from '@ory/kratos-client/api';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormInputText extends FormElement {
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
