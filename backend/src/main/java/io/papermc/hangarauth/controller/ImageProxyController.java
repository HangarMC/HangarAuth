package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/image")
public class ImageProxyController {

    private final RestTemplate restTemplate;
    private final GeneralConfig generalConfig;

    public ImageProxyController(final RestTemplate restTemplate, final GeneralConfig generalConfig) {
        this.restTemplate = restTemplate;
        this.generalConfig = generalConfig;
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> proxy(final HttpServletRequest request, final HttpServletResponse response) {
        final String url = this.cleanUrl(request.getRequestURI());
        return this.restTemplate.getForEntity(url, byte[].class);
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
