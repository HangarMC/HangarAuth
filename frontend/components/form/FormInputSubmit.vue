<template>
    <v-btn
        :disabled="disabledField || node.attributes.disabled"
        :name="node.attributes.name"
        type="text"
        :messages="messages"
        :error-messages="errorMessages"
        :value="node.attributes.value"
        persistent-hint
        block
        color="primary"
        class="mt-2"
    >
        {{ text }}
    </v-btn>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormInputSubmit extends FormElement {
    get text() {
        return this.node.meta.label ? this.node.meta.label.text : 'submit';
    }
}
</script>

<style scoped></style>
