package io.papermc.hangarauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.RandomStringUtils;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.config.custom.HydraConfig;
import io.papermc.hangarauth.config.custom.KratosConfig;
import io.papermc.hangarauth.controller.model.ConsentResponse;
import io.papermc.hangarauth.controller.model.Traits;
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
import sh.ory.kratos.api.V0alpha1Api;
import sh.ory.kratos.model.SelfServiceLogoutUrl;
import sh.ory.kratos.model.Session;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    private final AdminApi hydraClient;
    private final V0alpha1Api kratosClient;
    private final GeneralConfig generalConfig;
    private final KratosConfig kratosConfig;
    private final ObjectMapper mapper;

    @Autowired
    public OAuthController(final HydraConfig hydraConfig, final KratosConfig kratosConfig, final GeneralConfig generalConfig, ObjectMapper mapper) {
        this.hydraClient = new AdminApi(Configuration.getDefaultApiClient().setBasePath(hydraConfig.getAdminUrl()));
        this.kratosClient = new V0alpha1Api(sh.ory.kratos.Configuration.getDefaultApiClient().setBasePath(kratosConfig.getAdminUrl()));
        this.generalConfig = generalConfig;
        this.kratosConfig = kratosConfig;
        this.mapper = mapper;
    }

    @GetMapping("/login")
    public RedirectView login(@RequestParam(value = "login_challenge", required = false) String challenge,
                              @CookieValue(value = "ory_kratos_session", required = false) String sessionCookie,
                              @RequestHeader(value = "cookie", required = false) String cookieHeader,
                              HttpSession session, HttpServletRequest request) {
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

        // we use this if we need to go to login and retry
        String endpointAndQuery = request.getServletPath() + "?" + request.getQueryString();

        UriComponents requestUrl = UriComponentsBuilder.fromHttpUrl(loginRequest.getRequestUrl()).build();

        if ("login".equals(requestUrl.getQueryParams().getFirst("prompt"))) {
            log.debug("login: prompt=login");
            String state = requestUrl.getQueryParams().getFirst("hydra_login_state");
            if (StringUtils.isBlank(state)) {
                log.debug("login: redirecting to login cause no state");
                return redirectToLogin(session, endpointAndQuery);
            }

            if (!state.equals(session.getAttribute("ory_kratos_session"))) {
                log.debug("login: redirecting to login cause session mismatch");
                return redirectToLogin(session, endpointAndQuery);
            }
        }

        if (StringUtils.isBlank(sessionCookie)) {
            log.debug("login: redirecting to login cause no session");
            return redirectToLogin(session, endpointAndQuery);
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
        if (context instanceof Map map) {
            Map<String, Object> identity = (Map<String, Object>) map.get("identity");
            Traits traits = this.mapper.convertValue(identity.get("traits"), Traits.class);
            return traits.getUsername();
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
    public RedirectView logout(@RequestParam("logout_challenge") String challenge) {
        if (StringUtils.isBlank(challenge)) {
            // TODO proper error page
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logout flow could not be completed because no Logout Challenge was found in the HTTP request.");
        }

        // we don't actually need the details as we directly accept the request
        // try {
        //     log.debug("asking hydra for details");
        //     LogoutRequest logoutMetadata = hydraClient.getLogoutRequest(challenge);
        //     log.debug(String.valueOf(logoutMetadata));
        // } catch (ApiException e) {
        //     throw new RuntimeException(e);
        // }

        try {
            log.debug("accepting logout request");
            CompletedRequest redirect = hydraClient.acceptLogoutRequest(challenge);
            return new RedirectView(redirect.getRedirectTo());
        } catch (ApiException e) {
            log.warn("consent: Error on acceptLogoutRequest: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on acceptLogoutRequest: " + e.getResponseBody(), e);
        }
    }

    @GetMapping("/frontchannel-logout")
    public RedirectView frontchannelLogout(@RequestHeader(value = "cookie", required = false) String cookieHeader) {
        try {
            SelfServiceLogoutUrl redirect = kratosClient.createSelfServiceLogoutFlowUrlForBrowsers(cookieHeader);
            log.debug("front channel logout redirect {}", redirect.getLogoutUrl());
            return new RedirectView(redirect.getLogoutUrl());
        } catch (sh.ory.kratos.ApiException e) {
            log.warn("consent: Error on createSelfServiceLogoutFlowUrlForBrowsers: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error on createSelfServiceLogoutFlowUrlForBrowsers: " + e.getResponseBody(), e);
        }
    }

    private RedirectView redirectToLogin(HttpSession session, String endpointAndQuery) {
        String state = RandomStringUtils.randomAlphabetic(16);
        session.setAttribute("hydraLoginState", state);

        String returnTo = UriComponentsBuilder.fromHttpUrl(generalConfig.getPublicHost() + endpointAndQuery)
            .queryParam("hydra_login_state", state)
            .build().toUriString();
        String redirectTo = UriComponentsBuilder.fromHttpUrl(kratosConfig.getPublicUrl() + "/self-service/login/browser")
            .queryParam("refresh", "true")
            .queryParam("return_to", returnTo)
            .build().toUriString();

        log.debug("redirectToLogin: {}", redirectTo);

        return new RedirectView(redirectTo);
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
