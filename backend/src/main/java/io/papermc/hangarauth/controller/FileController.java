package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.service.ImageService;
import io.papermc.hangarauth.service.file.FileService;
import io.papermc.hangarauth.service.file.S3FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public abstract class FileController {

    protected final ImageService imageService;
    protected final FileService fileService;

    protected FileController(final ImageService imageService, final FileService fileService) {
        this.imageService = imageService;
        this.fileService = fileService;
    }

    protected ResponseEntity<?> downloadOrRedirect(final String path, final HttpServletRequest request, final HttpServletResponse response, final boolean url) {
        if (this.fileService instanceof S3FileService) {
            final String cdnPath = url ? this.imageService.getCdnPathFromUrl(path, request, response) : this.imageService.getCdnPathFromFile(path, request, response);
            try {
                return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(new URI(cdnPath != null ? cdnPath : this.fileService.getDownloadUrl(path)))
                    .lastModified(Instant.now())
                    .cacheControl(CacheControl.maxAge(Duration.of(4, ChronoUnit.HOURS)))
                    .build();
            } catch (final URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            final byte[] image = url ? this.imageService.getImageFromUrl(path, request, response) : this.imageService.getImageFromFile(path, request, response);
            return ResponseEntity.ok()
                .contentLength(image.length)
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .lastModified(Instant.now())
                .cacheControl(CacheControl.noCache())
                .body(new ByteArrayResource(image));
        }
    }
}
