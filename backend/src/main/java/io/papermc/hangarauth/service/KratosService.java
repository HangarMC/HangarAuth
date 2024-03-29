package io.papermc.hangarauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.papermc.hangarauth.config.custom.KratosConfig;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.db.dao.KratosIdentityDAO;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import sh.ory.kratos.ApiClient;
import sh.ory.kratos.ApiException;
import sh.ory.kratos.api.V0alpha1Api;
import sh.ory.kratos.model.AdminUpdateIdentityBody;
import sh.ory.kratos.model.Identity;
import sh.ory.kratos.model.SelfServiceSettingsFlow;

/**
 * For interacting with the kratos API
 */
@Service
public class KratosService {

    private static final Logger log = LoggerFactory.getLogger(KratosService.class);

    private final KratosIdentityDAO kratosIdentityDAO;
    private final ObjectMapper mapper;
    private final V0alpha1Api adminClient;
    private final V0alpha1Api publicClient;

    @Autowired
    public KratosService(final KratosIdentityDAO kratosIdentityDAO, final KratosConfig kratosConfig, final ObjectMapper mapper) {
        this.kratosIdentityDAO = kratosIdentityDAO;
        this.mapper = mapper;
        this.adminClient = new V0alpha1Api(new ApiClient().setBasePath(kratosConfig.adminUrl()));
        this.publicClient = new V0alpha1Api(new ApiClient().setBasePath(kratosConfig.publicBackendUrl()));
    }

    public @Nullable UUID getUserId(final String identifier) {
        return this.kratosIdentityDAO.getUserId(identifier);
    }

    public @NotNull Identity getUserIdentity(final @NotNull UUID userId) {
        try {
            final Identity identity = this.adminClient.adminGetIdentity(userId.toString());
            if (identity == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "how is the identity null here?");
            }
            return identity;
        } catch (final ApiException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public @NotNull Traits getTraits(final @NotNull UUID userId) {
        final Identity identity = this.getUserIdentity(userId);
        return this.mapper.convertValue(identity.getTraits(), Traits.class);
    }

    public @NotNull SelfServiceSettingsFlow getSettingsFlow(final @NotNull String flowId, final @NotNull String cookies) {
        try {
            return this.publicClient.getSelfServiceSettingsFlow(flowId, null, cookies);
        } catch (final ApiException e) {
            log.error("Error while getting settings flow (" + this.publicClient.getApiClient().getBasePath() + ")", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void checkCsrfToken(final @NotNull String flowId, final @NotNull String cookies, final String csrfToken) {
        if (csrfToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "no csrf token");
        }

        final SelfServiceSettingsFlow flow = this.getSettingsFlow(flowId, cookies);
        final Optional<String> flowCsrfToken = flow.getUi().getNodes().stream().filter(n -> n.getAttributes().getName().equals("csrf_token")).map(n -> n.getAttributes().getValue().toString()).findAny();
        if (flowCsrfToken.isEmpty() || !flowCsrfToken.get().equals(csrfToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "csrf token mismatch");
        }
    }

    public void setTraits(final UUID userId, final Traits newTraits) {
        try {
            this.adminClient.adminUpdateIdentity(userId.toString(), new AdminUpdateIdentityBody().traits(newTraits));
        } catch (final ApiException e) {
            log.error("Error while setting traits (" + this.adminClient.getApiClient().getBasePath() + ")", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void updateTraits(final UUID userId, final Traits updatedTraits) {
        final Traits traits = this.getTraits(userId);
        // copy old
        final String username = traits.username();
        final String email = traits.email();
        final String github = traits.github();
        final String discord = traits.discord();
        // copy over new if set
        final String language = StringUtils.hasText(updatedTraits.language()) ? updatedTraits.language() : traits.language();
        final String theme = StringUtils.hasText(updatedTraits.theme()) ? updatedTraits.theme() : traits.theme();
        // save new
        final Traits newTraits = new Traits(email, github, discord, language, username, theme);
        this.setTraits(userId, newTraits);
    }
}
