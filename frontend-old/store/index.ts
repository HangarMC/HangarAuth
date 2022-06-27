import { MutationTree } from 'vuex';
import { Identity } from '@ory/kratos-client/api';
import { AALInfo } from '~/plugins/kratos';

export interface RootState {
    user: Identity;
    aal: AALInfo;
    hydraData: string[];
}

export const state: () => RootState = () => ({
    user: null as unknown as RootState['user'],
    aal: null as unknown as RootState['aal'],
    hydraData: null as unknown as RootState['hydraData'],
});

export const mutations: MutationTree<RootState> = {
    SET_USER: (state, payload: RootState['user']) => {
        state.user = payload;
    },
    SET_AAL: (state, payload: RootState['aal']) => {
        state.aal = payload;
    },
    SET_HYDRA_DATA: (state, payload: RootState['hydraData']) => {
        state.hydraData = payload;
    },
};
