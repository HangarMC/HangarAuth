<template>
    <v-btn
        :disabled="disabledField || node.attributes.disabled"
        :name="node.attributes.name"
        type="text"
        :messages="messages"
        :error-messages="errorMessages"
        :value="node.attributes.value || ''"
        persistent-hint
        block
        color="secondary"
        class="mt-2"
        :onclick="node.attributes.onclick"
        @click.prevent
    >
        {{ text }}
    </v-btn>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormInputButton extends FormElement {
    get text() {
        return this.node.meta.label ? this.node.meta.label.text : 'submit';
    }
}
</script>

<style scoped></style>
