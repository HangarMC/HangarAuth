package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.service.KratosService;
import io.papermc.hangarauth.service.AvatarService;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    public static final MediaType WEBP = new MediaType("image", "webp");

    private static final Set<String> supportedTypes = Set.of("default", "user", "project");

    private final GeneralConfig generalConfig;
    private final KratosService kratosService;
    private final AvatarService avatarService;

    @Autowired
    public AvatarController(final GeneralConfig generalConfig, final KratosService kratosService, final AvatarService avatarService) {
        this.generalConfig = generalConfig;
        this.kratosService = kratosService;
        this.avatarService = avatarService;
    }

    @GetMapping(value = "/{type}/{subject}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getAvatarUrl(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject) {
        this.checkSupported(type);
        return this.avatarService.getAvatarUrl(type, subject);
    }

    // only really called if storage = local
    @GetMapping("/{type}/{subject}.webp")
    public ResponseEntity<ByteArrayResource> getAvatar(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject) throws IOException {
        this.checkSupported(type);
        final byte[] image = this.avatarService.getAvatar(type, subject);
        return ResponseEntity.ok()
            .contentLength(image.length)
            .contentType(WEBP)
            .lastModified(Instant.now())
            .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
            .body(new ByteArrayResource(image));
    }

    @PostMapping(value = "/{type}/{subject}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void setAvatar(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject, @RequestParam final String apiKey, @RequestParam final MultipartFile avatar) throws IOException {
        this.checkApiKey(apiKey);
        this.checkSupported(type);
        this.avatarService.setAvatar(type, subject, avatar.getBytes());
    }

    @PostMapping(value = "/{type}/{subject}/{flowId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void setAvatarKratos(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject, @PathVariable final String flowId, @RequestHeader("cookie") final String cookies, @RequestParam("csrf_token") final String csrfToken, @RequestParam final MultipartFile avatar) throws IOException {
        this.kratosService.checkCsrfToken(flowId, cookies, csrfToken);
        this.checkSupported(type);
        this.avatarService.setAvatar(type, subject, avatar.getBytes());
    }

    @DeleteMapping("/{type}/{subject}")
    public void deleteAvatar(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject, @RequestParam final String apiKey) {
        this.checkApiKey(apiKey);
        this.checkSupported(type);
        this.avatarService.deleteAvatar(type, subject);
    }

    @DeleteMapping("/{type}/{subject}/{flowId}")
    public void deleteAvatarKratos(@PathVariable final @NotNull String type, @PathVariable final @NotNull String subject, @PathVariable final String flowId, @RequestHeader("cookie") final String cookies, @RequestParam("csrf_token") final String csrfToken) {
        this.kratosService.checkCsrfToken(flowId, cookies, csrfToken);
        this.checkSupported(type);
        this.avatarService.deleteAvatar(type, subject);
    }

    private void checkSupported(final String type) {
        if (!supportedTypes.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported type " + type);
        }
    }

    private void checkApiKey(final String apiKey) {
        if (!this.generalConfig.apiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
