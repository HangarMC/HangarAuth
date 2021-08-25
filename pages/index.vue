<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card>
            <v-card-title>{{ $t('index.title') }}</v-card-title>
            <v-card-text>
                <p v-text="$t('index.text', [currentUserName])" />

                <v-alert v-if="!verified" type="warning">
                    Your account is not verified yet, <a @click="$kratos.verify()">click here</a> to change that!
                </v-alert>

                <v-list>
                    <v-list-item v-for="(item, idx) in actions" :key="idx" :href="item.href" :to="item.to">
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                        <v-list-item-icon>
                            <v-icon>{{ item.icon }}</v-icon>
                        </v-list-item-icon>
                    </v-list-item>
                </v-list>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="$kratos.logout()">{{ $t('general.logout') }}</v-btn>
            </v-card-actions>
        </v-card>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { AuthRequired } from '~/middleware/auth';
import { RootState } from '~/store';

@Component({})
@AuthRequired()
export default class IndexPage extends Vue {
    title = this.$t('index.title');

    get verified() {
        const user = (this.$store.state as RootState).user;
        if (!user || !user.verifiable_addresses) {
            return false;
        }
        for (const verifiableAddress of user.verifiable_addresses) {
            if (verifiableAddress.verified) {
                return true;
            }
        }
        return false;
    }

    get actions() {
        const actions = [];

        actions.push({ title: this.$t('index.download'), icon: 'mdi-download', href: 'https://papermc.io/downloads' });
        actions.push({ title: this.$t('index.forums'), icon: 'mdi-message-reply', href: 'https://papermc.io/forums/' });
        // TODO hangar url
        actions.push({ title: this.$t('index.plugins'), icon: 'mdi-power-plug', href: 'https://hangar.benndorf.dev/' });
        actions.push({ title: this.$t('index.upload'), icon: 'mdi-upload', href: 'https://hangar.benndorf.dev/new' });
        actions.push({ title: this.$t('index.manage'), icon: 'mdi-cog', to: '/account/settings' });

        return actions;
    }

    // TODO implement
    get currentUserName() {
        return 'Paper';
    }
}
</script>

<style lang="scss" scoped>
.reset-link {
    float: right;
    font-style: italic;
}
</style>
