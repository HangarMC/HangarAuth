<template>
    <v-btn
        :disabled="node.attributes.disabled"
        :name="node.attributes.name"
        :label="node.meta.label ? node.meta.label.text : null"
        type="text"
        :messages="messages"
        :error-messages="errorMessages"
        :value="node.attributes.value"
        persistent-hint
    >
        submit
    </v-btn>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormSubmit extends FormElement {}
</script>

<style scoped></style>
