package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.ImageService;
import io.papermc.hangarauth.service.file.FileService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/image")
public class ImageProxyController extends FileController {

    private final ImageConfig imageConfig;
    private final RestTemplate restTemplate;
    private final GeneralConfig generalConfig;

    public ImageProxyController(final ImageService imageService, final FileService fileService, final ImageConfig imageConfig, final RestTemplate restTemplate, final GeneralConfig generalConfig) {
        super(imageService, fileService);
        this.imageConfig = imageConfig;
        this.restTemplate = restTemplate;
        this.generalConfig = generalConfig;
    }

    @GetMapping("/**")
    public ResponseEntity<?> proxy(final HttpServletRequest request, final HttpServletResponse response) {
        final String url = this.cleanUrl(request.getRequestURI());

        final UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!this.imageConfig.whitelist().contains(components.getHost())) {
            return this.restTemplate.getForEntity(url, byte[].class);
        }

        return this.downloadOrRedirect(url, request, response, true);
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> evict(@RequestParam final String apiKey, final HttpServletRequest request) {
        if (!this.generalConfig.apiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final String url = this.cleanUrl(request.getRequestURI());

        final UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!this.imageConfig.whitelist().contains(components.getHost())) {
            return ResponseEntity.badRequest().build();
        }

        this.imageService.evictCache(url);
        return ResponseEntity.ok().build();
    }

    private String cleanUrl(final String url) {
        return url
            .replace("/image/", "")
            .replace("https:/", "https://")
            .replace("http:/", "http://")
            .replace(this.generalConfig.hangarFrontendHost(), this.generalConfig.hangarBackendHost())
            .replace(":///", "://");
    }
}
