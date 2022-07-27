import { defineNuxtConfig } from "nuxt";
import path from "node:path";
import VueI18n from "@intlify/vite-plugin-vue-i18n";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import EslintPlugin from "vite-plugin-eslint";
import prettier from "./src/lib/plugins/prettier";

const hangarHost = process.env.hangarHost || "http://localhost:3333";
const publicHost = process.env.publicHost || "http://localhost:3001";
const publicApi = process.env.publicApi || "http://localhost:8081";
const api = process.env.api || "http://localhost:8081";
const kratos = process.env.kratos || "http://localhost:4433";
const kratosPublic = process.env.kratosPublic || "http://localhost:4433";
const hydraPublic = process.env.hydraPublic || "http://localhost:4445";
const signupDisabled = process.env.signupDisabled;

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
  srcDir: "src",
  // todo make configurable
  runtimeConfig: {
    kratos,
    api,
    public: {
      hangarHost,
      publicHost,
      publicApi,
      kratosPublic,
      hydraPublic,
      signupDisabled: Boolean(signupDisabled)
    },
  },
  modules: [
    "nuxt-windicss",
    "@pinia/nuxt",
    "@nuxtjs-alt/proxy"
  ],
  vite: {
    plugins: [
      // https://github.com/antfu/unplugin-vue-components
      Components({
        // we don't want to import components, just icons
        dirs: ["none"],
        // auto import icons
        resolvers: [
          // https://github.com/antfu/vite-plugin-icons
          IconsResolver({
            componentPrefix: "icon",
            enabledCollections: ["mdi"],
          }),
        ],
        dts: "types/generated/icons.d.ts",
      }),

      // https://github.com/antfu/unplugin-icons
      Icons({
        autoInstall: true,
      }),

      // https://github.com/intlify/bundle-tools/tree/main/packages/vite-plugin-vue-i18n
      VueI18n({
        include: [path.resolve(__dirname, "src/locales/*.json")],
      }),

      // TODO fix this
      // EslintPlugin({
      //   fix: true,
      // }),
      prettier(),
    ],
  },
  proxy: {
    "/oauth/login": api,
    "/oauth/handleConsent": api,
    "/oauth/logout": api,
    "/oauth/frontchannel-logout": api,
    "/avatar": api,
    "/image": api,
    "/sync": api,
    "/settings": api,
  }
});
