<template>
    <div>
        <!-- this is a hardcoded way to display secrets differently -->
        <template v-if="secrets">
            <div>{{ node.meta.label.text }}</div>
            <v-row class="mt-2 mb-2">
                <v-col v-for="secret in secrets" :key="secret.id" cols="3">
                    <!-- 1050014 means used -->
                    <code>{{ secret.id === 1050014 ? 'Used' : secret.text }}</code>
                </v-col>
            </v-row>
        </template>
        <template v-else>
            <div>{{ node.meta.label.text }}</div>
            <div>{{ node.attributes.text.text }}</div>
        </template>
    </div>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { UiNodeTextAttributes } from '@ory/kratos-client/api';
import { FormElement } from '~/components/mixins/FormElement';

@Component
export default class FormText extends FormElement {
    get secrets() {
        return ((this.node.attributes as UiNodeTextAttributes).text.context as any)?.secrets;
    }
}
</script>

<style scoped></style>
