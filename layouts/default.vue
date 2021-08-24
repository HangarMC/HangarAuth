<template>
    <v-app>
        <Header />
        <v-main>
            <v-container>
                <v-row>
                    <nuxt />
                </v-row>
            </v-container>
        </v-main>
        <Footer />
    </v-app>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import Header from '~/components/Header.vue';
import Footer from '~/components/Footer.vue';

@Component({
    components: {
        Header,
        Footer,
    },
})
export default class DefaultLayout extends Vue {
    title = 'HangarAuth';
}
</script>
