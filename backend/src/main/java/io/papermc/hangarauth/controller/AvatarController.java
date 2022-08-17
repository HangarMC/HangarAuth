package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.controller.model.Traits;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.db.model.AvatarTable;
import io.papermc.hangarauth.service.AvatarService;
import io.papermc.hangarauth.service.ImageService;
import io.papermc.hangarauth.service.KratosService;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final KratosService kratosService;
    private final AvatarService avatarService;
    private final GeneralConfig generalConfig;
    private final ImageService imageService;

    @Autowired
    public AvatarController(KratosService kratosService, AvatarService avatarService, GeneralConfig generalConfig, ImageService imageService) {
        this.kratosService = kratosService;
        this.avatarService = avatarService;
        this.generalConfig = generalConfig;
        this.imageService = imageService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getAvatar(@NotNull @PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        return this.getAvatar0(id, request, response);
    }

    private ResponseEntity<?> getAvatar0(String id, HttpServletRequest request, HttpServletResponse response) {
        UUID uuid;
        AvatarTable avatarTable = null;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            uuid = this.kratosService.getUserId(id);
            if (uuid == null) {
                avatarTable = this.avatarService.getOrgAvatarTable(id);
                if (avatarTable == null) {
                    // could still be an org, lets just always use the letter
                    return getAvatarFallback(id, request, response);
                }
            }
        }

        if (avatarTable == null) {
            avatarTable = this.avatarService.getUsersAvatarTable(uuid);
        }
        if (avatarTable == null) {
            return getAvatarFallback(uuid, request, response);
        }
        Path userAvatarPath = this.avatarService.getAvatarFor(uuid == null ? id : uuid.toString(), avatarTable.getFileName());
        if (Files.notExists(userAvatarPath)) {
            if (uuid == null) {
                this.avatarService.deleteAvatarTable(id);
                return getAvatarFallback(id, request, response);
            } else {
                this.avatarService.deleteAvatarTable(uuid);
                return getAvatarFallback(uuid, request, response);
            }
        }
        byte[] image = imageService.getImage(userAvatarPath, request, response);
        return ResponseEntity.ok()
            .contentLength(image.length)
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .lastModified(Instant.now())
            .cacheControl(CacheControl.maxAge(Duration.of(4, ChronoUnit.HOURS)))
            .body(new ByteArrayResource(image));
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void setUsersAvatar(@NotNull @PathVariable UUID userId, @RequestParam String flowId, @RequestHeader("cookie") String cookies, @RequestParam("csrf_token") String csrfToken, @RequestParam MultipartFile avatar) throws IOException {
        this.kratosService.checkCsrfToken(flowId, cookies, csrfToken);
        this.avatarService.saveAvatar(userId, avatar);
    }

    @PostMapping(value = "/org/{orgName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void setOrgAvatar(@NotNull @PathVariable String orgName, @RequestParam String apiKey, @RequestParam MultipartFile avatar) throws IOException {
        if (!generalConfig.getApiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        this.avatarService.saveOrgAvatar(orgName, avatar);
    }

    private ResponseEntity<?> getAvatarFallback(@NotNull UUID userId, HttpServletRequest request, HttpServletResponse response) {
        final Traits user = this.kratosService.getTraits(userId);
        return getAvatarFallback(userId.toString(), user.email(), request, response);
    }

    private ResponseEntity<?> getAvatarFallback(@NotNull String id, HttpServletRequest request, HttpServletResponse response) {
        return getAvatarFallback(id, null, request, response);
    }

    private ResponseEntity<?> getAvatarFallback(@NotNull String id, @Nullable String email, HttpServletRequest request, HttpServletResponse response) {
        final byte[] image = avatarService.getFallbackAvatar(id, email, request, response);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(Duration.ofHours(4)))
            .body(image);
    }
}
