import colors from 'vuetify/es5/util/colors';
import { VuetifyPreset } from 'vuetify';

export default {
    icons: {
        iconfont: 'mdi',
    },
    theme: {
        default: 'dark',
        dark: true,
        disable: false,
        options: {
            customProperties: true,
        },
        themes: {
            dark: {
                anchor: colors.blue.lighten3,
                primary: colors.blue.darken2,
                accent: colors.grey.darken3,
                secondary: colors.amber.darken3,
                info: colors.lightBlue.base,
                warning: colors.orange.darken3,
                error: colors.deepOrange.accent4,
                success: colors.lightGreen.darken2,
            },
            light: {
                anchor: colors.blue.lighten3,
                primary: colors.blue.darken2,
                accent: colors.grey.darken3,
                secondary: colors.amber.darken3,
                info: colors.teal.lighten1,
                warning: colors.amber.base,
                error: colors.deepOrange.accent4,
                success: colors.green.accent3,
            },
        },
    },
} as Partial<VuetifyPreset>;
