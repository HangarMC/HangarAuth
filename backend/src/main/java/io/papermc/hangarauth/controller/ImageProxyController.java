package io.papermc.hangarauth.controller;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.ImageService;
import io.papermc.hangarauth.service.file.FileService;

@RestController
@RequestMapping("/image")
public class ImageProxyController extends FileController  {

    private final ImageConfig imageConfig;
    private final RestTemplate restTemplate;
    private final GeneralConfig generalConfig;

    public ImageProxyController(ImageService imageService, FileService fileService, ImageConfig imageConfig, RestTemplate restTemplate, GeneralConfig generalConfig) {
        super(imageService, fileService);
        this.imageConfig = imageConfig;
        this.restTemplate = restTemplate;
        this.generalConfig = generalConfig;
    }

    @GetMapping("/**")
    public ResponseEntity<?> proxy(HttpServletRequest request, HttpServletResponse response) {
        String url = cleanUrl(request.getRequestURI());

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!imageConfig.whitelist().contains(components.getHost())) {
            return restTemplate.getForEntity(url, byte[].class);
        }

        return downloadOrRedirect(url, request, response, true);
    }

    @DeleteMapping("/**")
    public ResponseEntity<?> evict(@RequestParam String apiKey, HttpServletRequest request) {
        if (!generalConfig.getApiKey().equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String url = cleanUrl(request.getRequestURI());

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!imageConfig.whitelist().contains(components.getHost())) {
            return ResponseEntity.badRequest().build();
        }

        imageService.evictCache(url);
        return ResponseEntity.ok().build();
    }

    private String cleanUrl(String url) {
        return url
            .replace("/image/", "")
            .replace("https:/", "https://")
            .replace("http:/", "https:/");
    }
}
