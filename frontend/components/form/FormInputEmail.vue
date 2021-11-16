<template>
    <v-text-field
        :disabled="node.attributes.disabled"
        :name="node.attributes.name"
        :required="node.attributes.required"
        :label="node.meta.label ? node.meta.label.text : null"
        :messages="messages"
        :error-messages="errorMessages"
        type="email"
        persistent-hint
        autocomplete="email"
        :value="node.attributes.value"
    ></v-text-field>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormInputEmail extends FormElement {}
</script>

<style scoped></style>
