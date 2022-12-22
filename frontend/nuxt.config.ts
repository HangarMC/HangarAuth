import path from "node:path";
import VueI18n from "@intlify/vite-plugin-vue-i18n";
import IconsResolver from "unplugin-icons/resolver";
import Icons from "unplugin-icons/vite";
import Components from "unplugin-vue-components/vite";
import { ProxyOptions } from "@nuxtjs-alt/proxy";
import prettier from "./src/lib/plugins/prettier";

const hangarHost = process.env.HANGAR_HOST || "http://localhost:3333";
const publicHost = process.env.PUBLIC_HOST || "http://localhost:3001";
const backendHost = process.env.BACKEND_HOST || "http://localhost:8081";
const kratos = process.env.KRATOS || "http://localhost:4433";
const kratosPublic = process.env.KRATOS_PUBLIC || "http://localhost:4433";
const signupDisabled = process.env.signupDisabled || false;

// https://v3.nuxtjs.org/api/configuration/nuxt.config
export default defineNuxtConfig({
  imports: {
    autoImport: false,
  },
  srcDir: "src",
  runtimeConfig: {
    kratos,
    backendHost,
    public: {
      hangarHost,
      publicHost,
      kratosPublic,
      signupDisabled: Boolean(signupDisabled),
    },
  },
  modules: ["nuxt-windicss", "@pinia/nuxt", "@nuxtjs-alt/proxy"],
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
  experimental: {
    writeEarlyHints: false,
  },
  typescript: {
    // typeCheck: "build", // TODO enable typechecking on build
    // typeCheck: true, // uncomment to run it at dev
  },
  nitro: {
    compressPublicAssets: true,
  },
  proxy: {
    enableProxy: true,
    proxies: {
      "/oauth/login": defineBackendProxy(),
      "/oauth/handleConsent": defineBackendProxy(),
      "/oauth/logout": defineBackendProxy(),
      "/oauth/frontchannel-logout": defineBackendProxy(),
      "/avatar": defineBackendProxy(),
      "/image": defineBackendProxy(),
      "/sync": defineBackendProxy(),
      "/settings": defineBackendProxy(),
      "/version-info": defineBackendProxy(),
      "/sessions": defineKratosProxy(),
    },
  },
});

function defineBackendProxy(): ProxyOptions {
  return {
    configure: (proxy, options) => {
      options.target = process.env.BACKEND_HOST || process.env.NITRO_BACKEND_HOST || "http://localhost:8081";
    },
    changeOrigin: true,
  };
}

function defineKratosProxy(): ProxyOptions {
  return {
    configure: (proxy, options) => {
      options.target = process.env.KRATOS || process.env.NITRO_KRATOS || "http://localhost:4433";
    },
    changeOrigin: true,
  };
}
