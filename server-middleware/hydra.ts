import crypto from 'crypto';
import { Configuration, Session, V0alpha1Api } from '@ory/kratos-client';
import { AdminApi as HydraAdminApi, Configuration as HydraConfiguration } from '@ory/hydra-client';
import express, { NextFunction, Request, Response } from 'express';
import { isString } from 'lodash-es';
import session from 'express-session';
import cookies from 'cookie-parser';
import bodyParser from 'body-parser';

const cookieSecret = process.env.cookieSecret || 'dum';
const httpsEnabled = process.env.cookieHttps || 'false';
const kratosPublic = process.env.kratos || 'http://localhost:4433';
const hydraAdmin = process.env.hydraAdmin || 'http://localhost:4445';
const baseUrl = process.env.publicHost || 'http://localhost:3001';

const app = express();

const hydraClient = new HydraAdminApi(new HydraConfiguration({ basePath: hydraAdmin }));
const kratosClient = new V0alpha1Api(new Configuration({ basePath: kratosPublic }));

declare module 'express-session' {
    interface SessionData {
        hydraLoginState: string;
    }
}

app.use(
    session({
        secret: cookieSecret,
        resave: false,
        saveUninitialized: true,
        cookie: { secure: httpsEnabled === 'true' },
    })
);
app.use(cookies());
app.use(bodyParser.urlencoded({ extended: true }));

app.get('/login', async (req, res, next) => {
    try {
        const challenge = req.query.login_challenge as string;
        if (!challenge || !isString(challenge)) {
            return next(new Error('Login flow could not be completed because no Login Challenge was found in the HTTP request.'));
        }

        console.debug('asking hydra for details');
        const { data: loginRequest } = await hydraClient.getLoginRequest(challenge);
        if (loginRequest.skip) {
            console.debug('hydra said we can skip');
            const { data: loginResponse } = await hydraClient.acceptLoginRequest(challenge, {
                subject: loginRequest.subject,
            });
            return res.redirect(String(loginResponse.redirect_to));
        }

        const requestUrl = new URL(loginRequest.request_url);

        if (requestUrl.searchParams.get('prompt') === 'login') {
            console.debug('prompt=login');
            const state = req.query.hydra_login_state as string;
            if (!state || !isString(state)) {
                console.debug('redirecting to login cause no state');
                return redirectToLogin(req, res, next);
            }

            if (state !== req.session.hydraLoginState) {
                console.debug('redirecting to login cause session missmatch');
                return redirectToLogin(req, res, next);
            }
        }

        const sessionCookie = req.cookies.ory_kratos_session;
        if (!sessionCookie) {
            console.debug('redirecting to login cause no session');
            return redirectToLogin(req, res, next);
        }

        console.debug('asking kratos for details');
        req.headers.host = kratosPublic.split('/')[2];
        const { data: kratosSession } = await kratosClient.toSession(undefined, undefined, req as { headers: { [name: string]: string } });
        const subject = kratosSession.identity.id;
        console.debug('telling hydra we fine');
        const { data: loginResponse } = await hydraClient.acceptLoginRequest(challenge, { subject, context: kratosSession });
        return res.redirect(String(loginResponse.redirect_to));
    } catch (e) {
        console.debug('error in get login', e);
        next();
    }
});

app.get('/consent', async (req, res, next) => {
    try {
        const challenge = req.query.consent_challenge as string;
        if (!challenge || !isString(challenge)) {
            return next(new Error('Consent flow could not be completed because no Consent Challenge was found in the HTTP request.'));
        }

        console.debug('asking hydra for details');
        const { data: consentRequest } = await hydraClient.getConsentRequest(challenge);
        if (consentRequest.skip) {
            console.log('hydra said we can skip');
            const { data: consentResponse } = await hydraClient.acceptConsentRequest(challenge, {
                grant_scope: consentRequest.requested_scope,
                grant_access_token_audience: consentRequest.requested_access_token_audience,
                session: createHydraSession(consentRequest.context as Session, consentRequest.requested_scope),
            });
            return res.redirect(String(consentResponse.redirect_to));
        }

        console.debug('render consent', consentRequest);
        res.setHeader('hydra', [
            challenge,
            'csrf token', // TODO csrf
            (consentRequest.context! as Session).identity.traits.username,
            consentRequest.client!.client_name!,
            consentRequest.requested_scope!.join(','),
            consentRequest.client!.policy_uri ?? '',
            consentRequest.client!.tos_uri ?? '',
        ]);
        next();
    } catch (e) {
        console.debug('error in get consent', e);
        next();
    }
});

app.post('/consent', async (req, res, next) => {
    try {
        const challenge = req.body.challenge;
        if (req.body.submit !== 'Allow access') {
            const { data: consentResponce } = await hydraClient.rejectConsentRequest(challenge, {
                error: 'access_denied',
                error_description: 'The resource owner denied the request',
            });
            return res.redirect(String(consentResponce.redirect_to));
        }

        let grantScope = req.body.grant_scope;
        if (!Array.isArray(grantScope)) {
            grantScope = [grantScope];
        }

        const { data: consentRequest } = await hydraClient.getConsentRequest(challenge);
        const { data: redirect } = await hydraClient.acceptConsentRequest(challenge, {
            grant_scope: grantScope,
            grant_access_token_audience: consentRequest.requested_access_token_audience,
            remember: Boolean(req.body.remember),
            remember_for: 3600,
            session: createHydraSession(consentRequest.context as Session, consentRequest.requested_scope),
        });
        console.log('consentRequest', consentRequest);
        console.log('redirect', redirect);
        return res.redirect(String(redirect.redirect_to));
    } catch (e) {
        console.debug('error in post consent', e);
        next();
    }
});

const redirectToLogin = (req: Request, res: Response, next: NextFunction) => {
    if (!req.session) {
        next(new Error('No express-session?'));
        return;
    }

    const state = crypto.randomBytes(48).toString('hex');
    req.session.hydraLoginState = state;
    req.session.save((error) => {
        if (error) {
            next(error);
            return;
        }
        // @ts-ignore
        const configBaseUrl = baseUrl && baseUrl !== '/' ? baseUrl : '';
        const ourBaseUrl = configBaseUrl || `${req.protocol}://${req.headers.host}`;
        console.log('create return url: ', baseUrl, configBaseUrl, ourBaseUrl);
        const returnTo = new URL('/oauth' + req.url, ourBaseUrl);
        returnTo.searchParams.set('hydra_login_state', state);

        const redirectTo = new URL(kratosPublic + '/self-service/login/browser', ourBaseUrl);
        redirectTo.searchParams.set('refresh', 'true');
        redirectTo.searchParams.set('return_to', returnTo.toString());

        res.redirect(redirectTo.toString());
    });
};

const createHydraSession = (context: Session, requestedScope: string[] = []) => {
    const verifiableAddresses = context.identity.verifiable_addresses || [];
    if (!requestedScope.includes('email') || verifiableAddresses.length === 0) {
        return {};
    }

    let traits = null;
    if (requestedScope.includes('profile')) {
        traits = context.identity.traits;
    }

    return {
        id_token: {
            email: verifiableAddresses[0].value as Object,
            traits,
        },
    };
};

module.exports = app;
