<template>
    <img :id="node.attributes.id" :src="node.attributes.src" :height="node.attributes.height" :width="node.attributes.width" :alt="node.meta.label.text" />
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormImg extends FormElement {}
</script>

<style scoped></style>
