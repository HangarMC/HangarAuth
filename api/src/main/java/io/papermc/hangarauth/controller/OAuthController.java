package io.papermc.hangarauth.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpSession;

import io.papermc.hangarauth.config.custom.HydraConfig;
import io.papermc.hangarauth.config.custom.KratosConfig;
import io.papermc.hangarauth.controller.model.ConsentResponse;
import sh.ory.hydra.ApiException;
import sh.ory.hydra.Configuration;
import sh.ory.hydra.api.AdminApi;
import sh.ory.hydra.model.AcceptConsentRequest;
import sh.ory.hydra.model.AcceptLoginRequest;
import sh.ory.hydra.model.CompletedRequest;
import sh.ory.hydra.model.ConsentRequest;
import sh.ory.hydra.model.ConsentRequestSession;
import sh.ory.hydra.model.LoginRequest;
import sh.ory.hydra.model.RejectRequest;
import sh.ory.kratos.api.V0alpha2Api;
import sh.ory.kratos.model.Session;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    private final AdminApi hydraClient;
    private final V0alpha2Api kratosClient;

    @Autowired
    public OAuthController(final HydraConfig hydraConfig, final KratosConfig kratosConfig) {
        this.hydraClient = new AdminApi(Configuration.getDefaultApiClient().setBasePath(hydraConfig.getAdminUrl()));
        this.kratosClient = new V0alpha2Api(sh.ory.kratos.Configuration.getDefaultApiClient().setBasePath(kratosConfig.getAdminUrl()));
    }

    @GetMapping("/login")
    public RedirectView login(@RequestParam(value = "login_challenge", required = false) String challenge, @CookieValue(value = "ory_kratos_session", required = false) String sessionCookie, @RequestHeader(value = "cookie", required = false) String cookieHeader, HttpSession session) {
        if (StringUtils.isBlank(challenge)) {
            // TODO proper error page
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login flow could not be completed because no Login Challenge was found in the HTTP request.");
        }

        log.debug("login: Asking hydra for details");
        LoginRequest loginRequest;
        try {
            loginRequest = hydraClient.getLoginRequest(challenge);
        } catch (ApiException e) {
            log.warn("login: Error on getLoginRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on getLoginRequest: " + e.getResponseBody(), e);
        }

        // never skip, cause we need to pass the kratos session to hydra so that we can include the traits in the access token later
        // if (loginRequest.getSkip() || "PaperMC".equals(loginRequest.getClient().getOwner())) {
        //     log.debug("{}: Hydra said we can skip (owner is {})", loginRequest.getSubject(), loginRequest.getClient().getOwner());
        //     try {
        //         CompletedRequest loginResponse = api.acceptLoginRequest(challenge, new AcceptLoginRequest().subject(loginRequest.getSubject()));
        //         return new RedirectView(loginResponse.getRedirectTo());
        //     } catch (ApiException e) {
        //         log.warn("Error on acceptLoginRequest: {}", e.getMessage());
        //         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on acceptLoginRequest: " + e.getResponseBody(), e);
        //     }
        // }

        UriComponents requestUrl = UriComponentsBuilder.fromHttpUrl(loginRequest.getRequestUrl()).build();

        if ("login".equals(requestUrl.getQueryParams().getFirst("prompt"))) {
            log.debug("login: prompt=login");
            String state = requestUrl.getQueryParams().getFirst("hydra_login_state");
            if (StringUtils.isBlank(state)) {
                log.debug("login: redirecting to login cause no state");
                return redirectToLogin();
            }

            if (!state.equals(session.getAttribute("ory_kratos_session"))) {
                log.debug("login: redirecting to login cause session mismatch");
                return redirectToLogin();
            }
        }

        if (StringUtils.isBlank(sessionCookie)) {
            log.debug("login: redirecting to login cause no session");
            return redirectToLogin();
        }

        log.debug("login: asking kratos for details");
        try {
            Session kratosSession = kratosClient.toSession(null, cookieHeader);
            UUID subject = kratosSession.getIdentity().getId();
            log.debug("login: telling hydra we fine");
            CompletedRequest loginResponse = hydraClient.acceptLoginRequest(challenge, new AcceptLoginRequest().subject(subject.toString()).context(kratosSession).remember(true));
            log.debug("login: got url from hydra {}", loginResponse.getRedirectTo());
            return new RedirectView(loginResponse.getRedirectTo());
        } catch (sh.ory.kratos.ApiException e) {
            log.warn("login: Error on toSession: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on toSession: " + e.getResponseBody(), e);
        } catch (ApiException e) {
            log.warn("login: Error on acceptLoginRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on acceptLoginRequest: " + e.getResponseBody(), e);
        }
    }

    @GetMapping("/handleConsent")
    public ConsentResponse consent(@RequestParam(value = "consent_challenge", required = false) String challenge) {
        if (StringUtils.isBlank(challenge)) {
            // TODO proper error page
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Consent flow could not be completed because no Consent Challenge was found in the HTTP request.");
        }

        log.debug("consent: asking hydra for details");
        ConsentRequest consentRequest;
        try {
            consentRequest = hydraClient.getConsentRequest(challenge);
        } catch (ApiException e) {
            log.warn("consent: Error on getConsentRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on getConsentRequest: " + e.getResponseBody(), e);
        }

        if ((consentRequest.getSkip() != null && consentRequest.getSkip()) || (consentRequest.getClient() != null && "PaperMC".equals(consentRequest.getClient().getOwner()))) {
            log.debug("consent: hydra said we can skip");
            try {
                CompletedRequest consentResponse = hydraClient.acceptConsentRequest(challenge, new AcceptConsentRequest()
                    .grantScope(consentRequest.getRequestedScope())
                    .grantAccessTokenAudience(consentRequest.getRequestedAccessTokenAudience())
                    .session(createHydraSession(consentRequest.getContext(), consentRequest.getRequestedScope())));
                return new ConsentResponse(consentResponse.getRedirectTo());
            } catch (ApiException e) {
                log.warn("consent: Error on acceptConsentRequest: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on acceptConsentRequest: " + e.getResponseBody(), e);
            }
        }

        log.debug("consent: render consent {}", consentRequest);

        return new ConsentResponse(
            null,
            challenge,
            "csrf token",  // TODO csrf
            getUserName(consentRequest.getContext()),
            consentRequest.getClient().getClientName(),
            consentRequest.getRequestedScope(),
            consentRequest.getClient().getPolicyUri(),
            consentRequest.getClient().getTosUri()
        );
    }

    private String getUserName(Object context) {
        if (context instanceof Session session) {
            Object traits = session.getIdentity().getTraits();
            // TODO return traits.getUsername();
            return "dum";
        }
        return "";
    }

    @PostMapping("/handleConsent")
    public RedirectView processConsent(@RequestParam String challenge, @RequestParam String submit, @RequestParam("grant_scope") List<String> grantScope, @RequestParam(defaultValue = "0") String remember) {
        if (!"Allow access".equals(submit)) {
            try {
                CompletedRequest consentResponse = hydraClient.rejectConsentRequest(challenge, new RejectRequest().error("access_denied").errorDescription("The resource owner denied the request"));
                return new RedirectView(consentResponse.getRedirectTo());
            } catch (ApiException e) {
                log.warn("consent: Error on rejectConsentRequest: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on rejectConsentRequest: " + e.getResponseBody(), e);
            }
        }

        ConsentRequest consentRequest;
        try {
            consentRequest = hydraClient.getConsentRequest(challenge);
        } catch (ApiException e) {
            log.warn("consent: Error on getConsentRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on rejectConsentRequest: " + e.getResponseBody(), e);
        }
        try {
            CompletedRequest redirect = hydraClient.acceptConsentRequest(challenge, new AcceptConsentRequest()
                .grantScope(grantScope)
                .grantAccessTokenAudience(consentRequest.getRequestedAccessTokenAudience())
                .remember("1".equals(remember))
                .rememberFor(3600L)
                .session(createHydraSession(consentRequest.getContext(), grantScope)));

            log.debug("consent: consentRequest {}", consentRequest);
            log.debug("consent: redirect {}", redirect);
            return new RedirectView(redirect.getRedirectTo());
        } catch (ApiException e) {
            log.warn("consent: Error on acceptConsentRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on acceptConsentRequest: " + e.getResponseBody(), e);
        }
    }

    @GetMapping("/logout")
    public void logout() {
        log.debug("logout");
        // TODO logout
        //
        //     try {
        //        const challenge = req.query.logout_challenge as string;
        //        if (!challenge || !isString(challenge)) {
        //            return next(new Error('Logout flow could not be completed because no Logout Challenge was found in the HTTP request.'));
        //        }
        //
        //        // we don't actually need the details as we directly accept the request
        //        // console.debug('asking hydra for details');
        //        // const { data: logoutMetadata } = await hydraClient.getLogoutRequest(challenge);
        //        // console.debug(logoutMetadata);
        //
        //        console.debug('accepting logout request');
        //        const { data: redirect } = await hydraClient.acceptLogoutRequest(challenge);
        //        return res.redirect(String(redirect.redirect_to));
        //    } catch (e) {
        //        console.debug('error in get logout', e);
        //        next();
        //    }
    }

    @GetMapping("/frontchannel-logout")
    public void frontchannelLogout() {
        log.debug("frontchannelLogout");
        // TODO frontchannelLogout
        // try {
        //        const { data: redirect } = await kratosClient.createSelfServiceLogoutFlowUrlForBrowsers(req.header('cookie'));
        //        console.debug('front channel logout redirect', redirect.logout_url);
        //        return res.redirect(String(redirect.logout_url));
        //    } catch (e) {
        //        console.debug('error in get frontchannel-logout', e);
        //        next();
        //    }
    }

    private RedirectView redirectToLogin() {
        log.debug("redirectToLogin");
        // TODO redirectToLogin
        // if (!req.session) {
        //        next(new Error('No express-session?'));
        //        return;
        //    }
        //
        //    const state = crypto.randomBytes(48).toString('hex');
        //    req.session.hydraLoginState = state;
        //    req.session.save((error) => {
        //        if (error) {
        //            next(error);
        //            return;
        //        }
        //        // @ts-ignore
        //        const configBaseUrl = baseUrl && baseUrl !== '/' ? baseUrl : '';
        //        const ourBaseUrl = configBaseUrl || `${req.protocol}://${req.headers.host}`;
        //        console.log('create return url: ', baseUrl, configBaseUrl, ourBaseUrl);
        //        const returnTo = new URL('/oauth' + req.url, ourBaseUrl);
        //        returnTo.searchParams.set('hydra_login_state', state);
        //
        //        const redirectTo = new URL(kratosPublic + '/self-service/login/browser', ourBaseUrl);
        //        redirectTo.searchParams.set('refresh', 'true');
        //        redirectTo.searchParams.set('return_to', returnTo.toString());
        //
        //        res.redirect(redirectTo.toString());
        //    });
        return null;
    }

    @SuppressWarnings("unchecked")
    private ConsentRequestSession createHydraSession(Object context, List<String> requestedScope) {
        if (context instanceof Map map) {
            Map<String, Object> identity = (Map<String, Object>) map.get("identity");
            List<String> verifiableAddresses = (List<String>) identity.get("verifiable_addresses");
            if (!requestedScope.contains("email") || verifiableAddresses.isEmpty()) {
                log.debug("createHydraSession: no email {} {}", requestedScope, context);
                return new ConsentRequestSession();
            }

            Map<String, String> traits = Map.of();
            if (requestedScope.contains("profile")) {
                traits = (Map<String, String>) identity.get("traits");
            }

            log.debug("createHydraSession: traits {}", traits);
            return new ConsentRequestSession().idToken(Map.of("email", verifiableAddresses.get(0), "traits", traits));
        }
        log.debug("createHydraSession no map? {}", context);
        return new ConsentRequestSession();
    }
}
