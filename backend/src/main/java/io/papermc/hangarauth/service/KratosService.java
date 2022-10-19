package io.papermc.hangarauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private final V0alpha1Api adminClient;
    private final V0alpha1Api publicClient;

    @Autowired
    public KratosService(final KratosIdentityDAO kratosIdentityDAO, final KratosConfig kratosConfig, final ObjectMapper mapper) {
        this.kratosIdentityDAO = kratosIdentityDAO;
        this.mapper = mapper;
        this.adminClient = new V0alpha1Api(Configuration.getDefaultApiClient().setBasePath(kratosConfig.getAdminUrl()));
        this.publicClient = new V0alpha1Api(Configuration.getDefaultApiClient().setBasePath(kratosConfig.getPublicBackendUrl()));
    }

    public @Nullable UUID getUserId(final String identifier) {
        return this.kratosIdentityDAO.getUserId(identifier);
    }

    public @NotNull Identity getUserIdentity(@NotNull final UUID userId) {
        try {
            Identity identity = adminClient.adminGetIdentity(userId.toString());
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
            return publicClient.getSelfServiceSettingsFlow(flowId, null, cookies);
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void checkCsrfToken(@NotNull final String flowId, @NotNull final String cookies, final String csrfToken) {
        if (csrfToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "no csrf token");
        }

        final SelfServiceSettingsFlow flow = this.getSettingsFlow(flowId, cookies);
        final Optional<String> flowCsrfToken = flow.getUi().getNodes().stream().filter(n -> n.getAttributes().getName().equals("csrf_token")).map(n -> n.getAttributes().getValue().toString()).findAny();
        if (flowCsrfToken.isEmpty() || !flowCsrfToken.get().equals(csrfToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "csrf token mismatch");
        }
    }

    public void setTraits(UUID userId, Traits newTraits) {
        try {
            adminClient.adminUpdateIdentity(userId.toString(), new AdminUpdateIdentityBody().traits(newTraits));
        } catch (ApiException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void updateTraits(UUID userId, Traits updatedTraits) {
        Traits traits = getTraits(userId);
        // copy old
        String username = traits.username();
        String email = traits.email();
        String github = traits.github();
        String discord = traits.discord();
        // copy over new if set
        String language = StringUtils.hasText(updatedTraits.language()) ? updatedTraits.language() : traits.language();
        String theme = StringUtils.hasText(updatedTraits.theme()) ? updatedTraits.theme() : traits.theme();
        // save new
        Traits newTraits = new Traits(email, github, discord, language, username, theme);
        setTraits(userId, newTraits);
    }
}
