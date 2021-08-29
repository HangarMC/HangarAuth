import { Context, Middleware } from '@nuxt/types';

const middleware: Middleware = ({ res, store }: Context) => {
    store.commit('SET_HYDRA_DATA', res.getHeader('hydra'));
    res.removeHeader('hydra');
};
export default middleware;
