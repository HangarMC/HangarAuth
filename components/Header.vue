<template>
    <v-app-bar fixed app>
        <v-menu bottom offset-y open-on-hover transition="slide-y-transition" close-delay="100">
            <template #activator="{ on, attrs }">
                <v-btn text x-large class="align-self-center px-1" v-bind="attrs" :ripple="false" v-on="on">
                    <NuxtLink
                        class="float-left"
                        :to="currentUser ? '/' : '/account/login'"
                        :event="$route.fullPath.startsWith('/account/login') ? '' : 'click'"
                        exact
                    >
                        <v-img height="55" width="220" src="https://papermc.io/images/logo-marker.svg" alt="Paper logo" />
                    </NuxtLink>

                    <v-icon>mdi-chevron-down</v-icon>
                </v-btn>
            </template>
            <Dropdown :controls="dropdown" />
        </v-menu>
    </v-app-bar>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import Dropdown, { Control } from '~/components/Dropdown.vue';

@Component({
    components: {
        Dropdown,
    },
})
export default class Header extends Vue {
    get dropdown(): Control[] {
        const controls: Control[] = [];
        controls.push({
            link: 'https://www.papermc.io',
            icon: 'mdi-home',
            title: this.$t('nav.hangar.home'),
        });
        controls.push({
            link: 'https://papermc.io/forums',
            icon: 'mdi-comment-multiple',
            title: this.$t('nav.hangar.forums'),
        });
        controls.push({
            link: 'https://github.com/PaperMC',
            icon: 'mdi-code-braces',
            title: this.$t('nav.hangar.code'),
        });
        controls.push({
            link: 'https://paper.readthedocs.io',
            icon: 'mdi-book',
            title: this.$t('nav.hangar.docs'),
        });
        controls.push({
            link: 'https://papermc.io/javadocs',
            icon: 'mdi-school',
            title: this.$t('nav.hangar.javadocs'),
        });
        controls.push({
            link: process.env.hangarHost,
            icon: 'mdi-home',
            title: this.$t('nav.hangar.hangar'),
        });
        controls.push({
            link: 'https://papermc.io/downloads',
            icon: 'mdi-download',
            title: this.$t('nav.hangar.downloads'),
        });
        controls.push({
            link: 'https://papermc.io/community',
            icon: 'mdi-comment',
            title: this.$t('nav.hangar.community'),
        });
        return controls;
    }

    get currentUser() {
        return this.$store.state.user;
    }
}
</script>

<style lang="scss"></style>
