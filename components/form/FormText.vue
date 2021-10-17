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
        :autocomplete="disableAutocomplete ? 'off' : undefined"
        :value="node.attributes.value"
    />
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormText extends FormElement {}
</script>

<style scoped></style>
