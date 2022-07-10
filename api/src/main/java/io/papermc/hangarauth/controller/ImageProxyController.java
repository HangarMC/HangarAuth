package io.papermc.hangarauth.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.papermc.hangarauth.config.custom.ImageConfig;
import io.papermc.hangarauth.service.ImageService;

@RestController
@RequestMapping("/image")
public class ImageProxyController {

    private final ImageService service;
    private final ImageConfig config;

    public ImageProxyController(ImageService service, ImageConfig config) {
        this.service = service;
        this.config = config;
    }

    @GetMapping("/**")
    public ResponseEntity<ByteArrayResource> proxy(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getRequestURI().replace("/image/", "");

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        if (!config.whitelist().contains(components.getHost())) {
            return ResponseEntity.badRequest().build();
        }

        byte[] image = service.getImage(url, request, response);
        return ResponseEntity.ok()
            .contentLength(image.length)
            .contentType(MediaType.parseMediaType(response.getContentType()))
            .body(new ByteArrayResource(image));
    }
}
