<template>
    <v-col md="6" offset-md="3" cols="12" offset="0">
        <v-card v-if="error">
            <v-card-title>
                <h5 class="subheading">Error processing consent request</h5>
            </v-card-title>
            <v-card-text>
                {{ error }}
            </v-card-text>
        </v-card>
        <v-form v-else action="/oauth/consent" method="POST">
            <input name="challenge" type="hidden" :value="challenge" />
            <input name="_csrf" type="hidden" :value="csrfToken" />
            <v-card>
                <v-card-title>
                    <h5 class="subheading">Consent Required!</h5>
                </v-card-title>
                <v-card-text>
                    <p style="margin-bottom: 15px">
                        <template v-if="user">Hi {{ user }}, application </template>
                        <template v-else>Application </template>
                        <strong>{{ clientName }}</strong> wants access resources on your behalf and to:
                    </p>

                    <div style="margin-bottom: 15px; margin-top: 8px">Scopes:</div>
                    <div v-for="scope in requestedScope" :key="scope" style="margin-bottom: 5px">
                        <input :id="scope" style="display: inline-block; margin-right: 3px" type="checkbox" :value="scope" name="grant_scope" checked />
                        <div style="display: inline-block">{{ scope }}</div>
                    </div>

                    <ul>
                        <li v-if="policyUrl">
                            <a :href="policyUrl">Read the Privacy Policy</a>
                        </li>
                        <li v-if="tosUri">
                            <a :href="tosUri">Terms of Service</a>
                        </li>
                    </ul>

                    <div>
                        <input id="remember" type="checkbox" value="1" name="remember" checked />
                        <div style="display: inline-block; margin-right: 3px">Do not ask me again</div>
                    </div>

                    <v-btn id="accept" color="primary" type="submit" name="submit" value="Allow access">Allow access</v-btn>
                    <v-btn id="deny" color="error" type="submit" name="submit" value="Deny access">Deny access</v-btn>
                </v-card-text>
            </v-card>
        </v-form>
    </v-col>
</template>

<script lang="ts">
import { Component, Vue } from 'nuxt-property-decorator';
import { RootState } from '~/store';

@Component({ middleware: ['passHeader'] })
export default class ConsentPage extends Vue {
    get hydraData() {
        return (this.$store.state as RootState).hydraData;
    }

    get error() {
        return this.hydraData && this.hydraData.length === 1 ? this.hydraData[0] : null;
    }

    get challenge() {
        return this.hydraData[0];
    }

    get csrfToken() {
        return this.hydraData[1];
    }

    get user() {
        return this.hydraData[2];
    }

    get clientName() {
        return this.hydraData[3];
    }

    get requestedScope() {
        return this.hydraData[4]?.split(',');
    }

    get policyUrl() {
        return this.hydraData[5];
    }

    get tosUri() {
        return this.hydraData[6];
    }
}
</script>
