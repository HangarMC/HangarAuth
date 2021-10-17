<template>
    <v-btn
        :disabled="node.attributes.disabled"
        :name="node.attributes.name"
        type="text"
        :messages="messages"
        :error-messages="errorMessages"
        :value="node.attributes.value"
        persistent-hint
        block
        color="primary"
    >
        {{ text }}
    </v-btn>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormSubmit extends FormElement {
    get text() {
        return this.node.meta.label ? this.node.meta.label.text : 'submit';
    }
}
</script>

<style scoped></style>
