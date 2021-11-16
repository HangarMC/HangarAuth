<template>
    <input v-bind="node.attributes" />
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormInputText extends FormElement {}
</script>

<style scoped></style>
