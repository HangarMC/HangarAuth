import { NuxtConfig } from '@nuxt/types';
import colors from 'vuetify/lib/util/colors';

import en from './locales/en';
import fr from './locales/fr';

require('events').EventEmitter.defaultMaxListeners = 20;
require('dotenv').config();

const publicHost = process.env.PUBLIC_HOST || 'http://localhost:3001';
const kratos = process.env.kratos || 'http://localhost:3001';
const hydraAdmin = process.env.hydraAdmin || 'http://localhost:3001';
const host = process.env.host || 'localhost';
const cookieSecret = process.env.SECRET_COOKIE || 'dum';
const cookieHttps = process.env.cookieHttps || 'false';

export default {
    debug: true,
    telemetry: false,
    modern: 'server',
    target: 'server',
    head: {
        htmlAttrs: {
            dir: 'ltr',
        },
        titleTemplate: (titleChunk: string) => {
            return titleChunk ? `${titleChunk} | HangarAuth` : 'HangarAuth';
        },
        meta: [
            { charset: 'utf-8' },
            { name: 'viewport', content: 'width=device-width, initial-scale=1' },
            { hid: 'description', name: 'description', content: '' },
        ],
    },

    // Global CSS: https://go.nuxtjs.dev/config-css
    css: ['~/assets/main.scss'],

    // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
    plugins: ['~/plugins/vuetify.ts', '~/plugins/kratos.ts'],

    // Auto import components: https://go.nuxtjs.dev/config-components
    components: false,

    // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
    buildModules: [
        // https://go.nuxtjs.dev/typescript
        '@nuxt/typescript-build',
        // https://go.nuxtjs.dev/vuetify
        '@nuxtjs/vuetify',
        // https://go.nuxtjs.dev/eslint
        '@nuxtjs/eslint-module',
        '@nuxtjs/dotenv',
    ],

    // Modules: https://go.nuxtjs.dev/config-modules
    modules: [
        // https://go.nuxtjs.dev/axios
        '@nuxtjs/axios',
        '@nuxtjs/proxy',
        'nuxt-i18n',
    ],

    // Axios module configuration: https://go.nuxtjs.dev/config-axios
    axios: {},

    // PWA module configuration: https://go.nuxtjs.dev/pwa
    pwa: {
        manifest: {
            name: 'HangarAuth | PaperMC',
            short_name: 'HangarAuth',
            description: 'PaperMC Authentication Gateway!',
            lang: 'en',
        },
    },

    // Vuetify module configuration: https://go.nuxtjs.dev/config-vuetify
    vuetify: {
        customVariables: ['~/assets/variables.scss'],
        optionsPath: '~/plugins/vuetify.ts',
        treeShake: true,
    },

    // Build Configuration: https://go.nuxtjs.dev/config-build
    build: {
        transpile: ['lodash-es'],
    },

    router: {
        middleware: [],
    },
    serverMiddleware: [{ path: '/oauth', handler: '~/server-middleware/hydra.ts' }],

    i18n: {
        vueI18nLoader: true,
        strategy: 'no_prefix',
        defaultLocale: 'en',
        locales: [
            { code: 'fr', iso: 'fr-FR', name: 'Français' },
            { code: 'en', iso: 'en-US', name: 'English' },
        ],
        vueI18n: {
            locale: 'en',
            fallbackLocale: 'en',
            messages: {
                en,
                fr,
            },
        },
    },

    server: {
        port: 3001,
        host,
    },

    env: {
        kratos,
        hydraAdmin,
        publicHost,
        cookieSecret,
        cookieHttps,
    },

    loading: {
        color: colors.blue.lighten2,
        continuous: true,
    },

    publicRuntimeConfig: {
        axios: {
            browserBaseURL: publicHost,
        },
    },
} as NuxtConfig;
