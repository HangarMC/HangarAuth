import { MutationTree } from 'vuex';
import { Identity } from '@ory/kratos-client/api';

export interface RootState {
    user: Identity;
    hydraData: string[];
}

export const state: () => RootState = () => ({
    user: (null as unknown) as RootState['user'],
    hydraData: (null as unknown) as RootState['hydraData'],
});

export const mutations: MutationTree<RootState> = {
    SET_USER: (state, payload: RootState['user']) => {
        state.user = payload;
    },
    SET_HYDRA_DATA: (state, payload: RootState['hydraData']) => {
        state.hydraData = payload;
    },
};
