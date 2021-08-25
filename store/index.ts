import { MutationTree } from 'vuex';
import { Identity } from '@ory/kratos-client/api';

export interface RootState {
    user: Identity;
}

export const state: () => RootState = () => ({
    user: (null as unknown) as RootState['user'],
});

export const mutations: MutationTree<RootState> = {
    SET_USER: (state, payload: RootState['user']) => {
        state.user = payload;
    },
};
