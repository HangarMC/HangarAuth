package io.papermc.hangarauth.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.ImageService;

@RestController
@RequestMapping("/image")
public class ImageProxyController {

    private final ImageService service;
    private final ImageConfig config;
    private final RestTemplate restTemplate;
    private final GeneralConfig generalConfig;

    public ImageProxyController(ImageService service, ImageConfig config, RestTemplate restTemplate, GeneralConfig generalConfig) {
        this.service = service;
        this.config = config;
        this.restTemplate = restTemplate;
        this.generalConfig = generalConfig;
    }

    @GetMapping("/**")
    public ResponseEntity<?> proxy(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getRequestURI().replace("/image/", "");

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!config.whitelist().contains(components.getHost())) {
            return restTemplate.getForEntity(url, byte[].class);
        }

        byte[] image = service.getImage(url, request, response);
        return ResponseEntity.ok()
            .contentLength(image.length)
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .lastModified(Instant.now())
            .cacheControl(CacheControl.maxAge(Duration.of(4, ChronoUnit.HOURS)))
            .body(new ByteArrayResource(image));
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> evict(@RequestParam String apiKey, HttpServletRequest request) {
        if (!generalConfig.getApiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String url = request.getRequestURI().replace("/image/", "");

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!config.whitelist().contains(components.getHost())) {
            return ResponseEntity.badRequest().build();
        }

        service.evictCache(url);
        return ResponseEntity.ok().build();
    }
}
