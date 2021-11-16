<template>
    <div>
        <div>{{ node.meta.label.text }}</div>
        <div>{{ node.attributes.text.text }}</div>
    </div>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormText extends FormElement {}
</script>

<style scoped></style>
