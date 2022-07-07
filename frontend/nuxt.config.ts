import { defineNuxtConfig } from "nuxt";
import path from "node:path";
import VueI18n from "@intlify/vite-plugin-vue-i18n";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import EslintPlugin from "vite-plugin-eslint";
import prettier from "./src/lib/plugins/prettier";

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
  srcDir: "src",
  // todo make configurable
  runtimeConfig: {
    kratos: "http://localhost:4433",
    public: {
      kratosPublic: "http://localhost:4433",
      hangarHost: "http://localhost:3333",
      signupDisabled: false,
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
    "/oauth/login": "http://localhost:8081",
    "/oauth/handleConsent": "http://localhost:8081",
    "/oauth/logout": "http://localhost:8081",
    "/oauth/frontchannel-logout": "http://localhost:8081",
  }
});
