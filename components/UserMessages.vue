<template>
    <div>
        <v-alert v-for="message in ui.messages" :key="message.id" :type="message.type">
            {{ message.text }}
        </v-alert>
    </div>
</template>

<script lang="ts">
import { Component } from 'nuxt-property-decorator';
import { KratosUiProp } from '~/components/mixins/Kratos';

@Component
export default class UserMessages extends KratosUiProp {}
</script>
