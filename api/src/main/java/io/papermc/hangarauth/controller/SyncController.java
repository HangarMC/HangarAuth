package io.papermc.hangarauth.controller;

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

import java.util.UUID;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.KratosService;

@RestController
@RequestMapping("/sync")
public class SyncController {

    private final GeneralConfig generalConfig;
    private final KratosService kratosService;

    @Autowired
    public SyncController(GeneralConfig generalConfig, KratosService kratosService) {
        this.generalConfig = generalConfig;
        this.kratosService = kratosService;
    }

    @PostMapping(value = "/user/{user}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void syncUserTraits(@NotNull @PathVariable String user, @RequestParam String apiKey, @RequestBody Traits updatedTraits) {
        if (!generalConfig.getApiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UUID userId = UUID.fromString(user);
        this.kratosService.updateTraits(userId, updatedTraits);
    }
}
