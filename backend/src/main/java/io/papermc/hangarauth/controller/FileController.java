package io.papermc.hangarauth.controller;

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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.service.ImageService;
import io.papermc.hangarauth.service.file.FileService;
import io.papermc.hangarauth.service.file.S3FileService;

public abstract class FileController {

    protected final ImageService imageService;
    protected final FileService fileService;

    protected FileController(ImageService imageService, FileService fileService) {
        this.imageService = imageService;
        this.fileService = fileService;
    }

    protected ResponseEntity<?> downloadOrRedirect(String path, HttpServletRequest request, HttpServletResponse response, boolean url) {
        if (fileService instanceof S3FileService) {
            String cdnPath = url ? imageService.getCdnPathFromUrl(path, request, response) : imageService.getCdnPathFromFile(path, request, response);
            try {
                return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(new URI(cdnPath != null ? cdnPath : fileService.getDownloadUrl(path)))
                    .lastModified(Instant.now())
                    .cacheControl(CacheControl.maxAge(Duration.of(4, ChronoUnit.HOURS)))
                    .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            byte[] image = url ? imageService.getImageFromUrl(path, request, response) : imageService.getImageFromFile(path, request, response);
            return ResponseEntity.ok()
                .contentLength(image.length)
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .lastModified(Instant.now())
                .cacheControl(CacheControl.maxAge(Duration.of(4, ChronoUnit.HOURS)))
                .body(new ByteArrayResource(image));
        }
    }
}
