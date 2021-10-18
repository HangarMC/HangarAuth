<template>
    <v-text-field
        :disabled="node.attributes.disabled"
        :name="node.attributes.name"
        :required="node.attributes.required"
        :label="node.meta.label ? node.meta.label.text : null"
        :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
        :type="show ? 'text' : 'password'"
        :messages="messages"
        :error-messages="errorMessages"
        persistent-hint
        :autocomplete="disableAutocomplete ? 'new-password' : 'current-password'"
        @click:append="show = !show"
    ></v-text-field>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormPassword extends FormElement {
    show: boolean = false;
}
</script>

<style scoped></style>
