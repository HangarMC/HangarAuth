import { Context } from '@nuxt/types';

export default ({ $kratos, route }: Context) => {
    console.log('kratos');
    if (!route.meta || !route.meta.authRequired) {
        return;
    }

    return $kratos.client
        .toSession(undefined, undefined, { withCredentials: true })
        .then((session) => {
            console.log('whoami', session.data);
        })
        .catch((e) => {
            if (e.response.status === 401) {
                return $kratos.login();
            }
            console.log(e);
        });
};
