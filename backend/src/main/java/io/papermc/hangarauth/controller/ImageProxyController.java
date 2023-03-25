package io.papermc.hangarauth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/image")
public class ImageProxyController {

    private final RestTemplate restTemplate;

    public ImageProxyController(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> proxy(final HttpServletRequest request, final HttpServletResponse response) {
        final String url = this.cleanUrl(request.getRequestURI());
        if (this.validTarget(url)) {
            return this.restTemplate.getForEntity(url, byte[].class);
        } else {
           return ResponseEntity.badRequest().build();
        }
    }

    private String cleanUrl(final String url) {
        return url
            .replace("/image/", "")
            .replace("https:/", "https://")
            .replace("http:/", "http://")
            .replace(":///", "://");
    }

    private boolean validTarget(final String url) {
        try {
            final URL parsedUrl = new URL(url);
            // valid proto
            if (!parsedUrl.getProtocol().equals("http") && !parsedUrl.getProtocol().equals("https")) {
                return false;
            }
            final InetAddress inetAddress = InetAddress.getByName(parsedUrl.getHost());
            // not local ip
            if (inetAddress.isAnyLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isSiteLocalAddress()) {
                return false;
            }
        } catch (final MalformedURLException | UnknownHostException e) {
            return false;
        }

        return true;
    }
}
