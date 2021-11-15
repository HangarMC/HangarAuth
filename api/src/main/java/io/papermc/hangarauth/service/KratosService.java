package io.papermc.hangarauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.papermc.hangarauth.config.custom.KratosConfig;
import io.papermc.hangarauth.controller.model.Traits;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sh.ory.kratos.ApiClient;
import sh.ory.kratos.ApiException;
import sh.ory.kratos.api.V0alpha1Api;
import sh.ory.kratos.model.Identity;
import sh.ory.kratos.model.JsonError;
import sh.ory.kratos.model.SelfServiceSettingsFlow;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * For interacting with the kratos admin API
 */
@Service
public class KratosService {

    private final KratosConfig kratosConfig;
    private final RestTemplate restTemplate;
    private final Gson gson;
    private final ObjectMapper mapper;

    @Autowired
    public KratosService(KratosConfig kratosConfig, RestTemplate restTemplate, @Name("kratos") Gson gson, ObjectMapper mapper) {
        this.kratosConfig = kratosConfig;
        this.restTemplate = restTemplate;
        this.gson = gson;
        this.mapper = mapper;
    }

    public @NotNull Identity getUserIdentity(@NotNull UUID userId) throws IOException {
        try {
            Identity identity = this.gson.fromJson(this.restTemplate.getForObject(this.kratosConfig.getAdminUrl() + "/identities/{user_id}", String.class, Map.of("user_id", userId)), Identity.class);
            if (identity == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "how is the identity null here?");
            }
            return identity;
        } catch (HttpClientErrorException clientErrorException) {
            JsonError error = this.mapper.readValue(clientErrorException.getResponseBodyAsByteArray(), JsonError.class);
            throw new ResponseStatusException(HttpStatus.valueOf(Objects.requireNonNullElse(error.getError().getCode(), 400L).intValue()), error.getError().getMessage(), clientErrorException);
        }
    }

    public @NotNull Traits getTraits(@NotNull UUID userId) throws IOException {
        Identity identity = this.getUserIdentity(userId);
        return this.mapper.convertValue(identity.getTraits(), Traits.class);
    }

    public @NotNull SelfServiceSettingsFlow getSettingsFlow(@NotNull String flowId, @NotNull String sessionToken) throws IOException {
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("X-Session-Cookie", sessionToken);
            final ResponseEntity<String> stringResponseEntity = this.restTemplate.exchange(this.kratosConfig.getPublicUrl() + "/self-service/settings/flows?id={flow_id}", HttpMethod.GET, new HttpEntity<>(headers), String.class, Map.of("flow_id", flowId));
            if (!stringResponseEntity.getStatusCode().is2xxSuccessful() || stringResponseEntity.getBody() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, stringResponseEntity.toString());
            }
            return this.gson.fromJson(stringResponseEntity.getBody(), SelfServiceSettingsFlow.class);
        } catch (HttpClientErrorException clientErrorException) {
            JsonError error = this.mapper.readValue(clientErrorException.getResponseBodyAsByteArray(), JsonError.class);
            throw new ResponseStatusException(HttpStatus.valueOf(Objects.requireNonNullElse(error.getError().getCode(), 400L).intValue()), error.getError().getMessage(), clientErrorException);
        }
    }

    public void checkCsrfToken(@NotNull String flowId, @NotNull String sessionToken, @NotNull String csrfToken) throws IOException {
        final SelfServiceSettingsFlow flow = getSettingsFlow(flowId, sessionToken);
        final Optional<String> flowCsrfToken = flow.getUi().getNodes().stream().filter(n -> n.getAttributes().getName().equals("csrf_token")).map(n -> n.getAttributes().getValue().toString()).findAny();
        if (flowCsrfToken.isEmpty() || !flowCsrfToken.get().equals(csrfToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "csrf token mismatch");
        }
    }
}
