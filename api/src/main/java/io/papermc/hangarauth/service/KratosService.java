package io.papermc.hangarauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import io.papermc.hangarauth.config.custom.KratosConfig;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.db.dao.KratosIdentityDAO;
import sh.ory.kratos.ApiException;
import sh.ory.kratos.Configuration;
import sh.ory.kratos.api.V0alpha1Api;
import sh.ory.kratos.model.AdminUpdateIdentityBody;
import sh.ory.kratos.model.Identity;
import sh.ory.kratos.model.SelfServiceSettingsFlow;

/**
 * For interacting with the kratos admin API
 */
@Service
public class KratosService {

    private final KratosIdentityDAO kratosIdentityDAO;
    private final ObjectMapper mapper;
    private final V0alpha1Api kratosClient;

    @Autowired
    public KratosService(final KratosIdentityDAO kratosIdentityDAO, final KratosConfig kratosConfig, final ObjectMapper mapper) {
        this.kratosIdentityDAO = kratosIdentityDAO;
        this.mapper = mapper;
        this.kratosClient = new V0alpha1Api(Configuration.getDefaultApiClient().setBasePath(kratosConfig.getAdminUrl()));
    }

    public @Nullable UUID getUserId(final String identifier) {
        return this.kratosIdentityDAO.getUserId(identifier);
    }

    public @NotNull Identity getUserIdentity(@NotNull final UUID userId) {
        try {
            Identity identity = kratosClient.adminGetIdentity(userId.toString());
            if (identity == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "how is the identity null here?");
            }
            return identity;
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public @NotNull Traits getTraits(@NotNull final UUID userId) {
        final Identity identity = this.getUserIdentity(userId);
        return this.mapper.convertValue(identity.getTraits(), Traits.class);
    }

    public @NotNull SelfServiceSettingsFlow getSettingsFlow(@NotNull final String flowId, @NotNull final String cookies) {
        try {
            return kratosClient.getSelfServiceSettingsFlow(flowId, null, cookies);
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void checkCsrfToken(@NotNull final String flowId, @NotNull final String cookies, @NotNull final String csrfToken) {
        final SelfServiceSettingsFlow flow = this.getSettingsFlow(flowId, cookies);
        final Optional<String> flowCsrfToken = flow.getUi().getNodes().stream().filter(n -> n.getAttributes().getName().equals("csrf_token")).map(n -> n.getAttributes().getValue().toString()).findAny();
        if (flowCsrfToken.isEmpty() || !flowCsrfToken.get().equals(csrfToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "csrf token mismatch");
        }
    }

    public void setTraits(UUID userId, Traits newTraits) {
        try {
            kratosClient.adminUpdateIdentity(userId.toString(), new AdminUpdateIdentityBody().traits(newTraits));
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
