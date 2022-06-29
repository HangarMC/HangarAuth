<template>
    <v-app-bar fixed app>
        <v-menu bottom offset-y open-on-hover transition="slide-y-transition" close-delay="100">
            <template #activator="{ on, attrs }">
                <Button size="large" class="align-self-center px-1" v-bind="attrs" v-on="on">
                    <NuxtLink
                        class="float-left"
                        :to="authStore.user ? '/' : '/account/login'"
                        :event="$route.fullPath.startsWith('/account/login') ? '' : 'click'"
                        exact
                    >
                        <img height="55" width="220" src="https://papermc.io/images/logo-marker.svg" alt="Paper logo" />
                    </NuxtLink>

                    <IconMdiChevronDown />
                </Button>
            </template>
            <Dropdown :controls="dropdown" />
        </v-menu>
    </v-app-bar>
</template>

<script lang="ts" setup>
import { useI18n } from "vue-i18n";
import { computed } from "vue";
import Button from "~/lib/components/design/Button.vue";
import { Control } from "~/components/Dropdown.vue";
import { useAuthStore } from "~/store/useAuthStore";

const { t } = useI18n();

const authStore = useAuthStore();

const dropdown = computed<Control[]>(() => {
  const controls: Control[] = [];
  controls.push({
    link: 'https://papermc.io/',
    icon: 'mdi-home',
    title: t('nav.hangar.home'),
  }, {
    link: 'https://papermc.io/forums',
    icon: 'mdi-comment-multiple',
    title: t('nav.hangar.forums'),
  }, {
    link: 'https://github.com/PaperMC',
    icon: 'mdi-code-braces',
    title: t('nav.hangar.code'),
  }, {
    link: 'https://paper.readthedocs.io',
    icon: 'mdi-book',
    title: t('nav.hangar.docs'),
  }, {
    link: 'https://papermc.io/javadocs',
    icon: 'mdi-school',
    title: t('nav.hangar.javadocs'),
  }, {
    link: process.env.hangarHost,
    icon: 'mdi-puzzle',
    title: t('nav.hangar.hangar'),
  }, {
    link: 'https://papermc.io/downloads',
    icon: 'mdi-download',
    title: t('nav.hangar.downloads'),
  }, {
    link: 'https://papermc.io/community',
    icon: 'mdi-comment',
    title: t('nav.hangar.community'),
  }, {
    link: '/',
    icon: 'mdi-key',
    title: t('nav.hangar.auth'),
  });
  return controls;
})
</script>
