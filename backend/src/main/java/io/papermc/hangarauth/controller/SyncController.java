package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.KratosService;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final GeneralConfig generalConfig;
    private final KratosService kratosService;

    @Autowired
    public SyncController(final GeneralConfig generalConfig, final KratosService kratosService) {
        this.generalConfig = generalConfig;
        this.kratosService = kratosService;
    }

    @PostMapping(value = "/user/{user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void syncUserTraits(@PathVariable final @NotNull String user, @RequestParam final String apiKey, @RequestBody final Traits updatedTraits) {
        if (!this.generalConfig.apiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        final UUID userId = UUID.fromString(user);
        this.kratosService.updateTraits(userId, updatedTraits);
    }
}
