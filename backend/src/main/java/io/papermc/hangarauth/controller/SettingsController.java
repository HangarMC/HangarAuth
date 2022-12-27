package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.KratosService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
public class SettingsController {

    private final KratosService kratosService;

    @Autowired
    public SettingsController(final KratosService kratosService) {
        this.kratosService = kratosService;
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveSettings(@PathVariable final @NotNull String userId,
                             @RequestParam(required = false) final String flowId,
                             @RequestHeader(value = "cookie", required = false) final String cookies,
                             @RequestParam(value = "csrf_token", required = false) final String csrfToken,
                             @RequestParam final String language, @RequestParam final String theme, final HttpServletResponse response) {
        if (!userId.equals("anon")) {
            this.kratosService.checkCsrfToken(flowId, cookies, csrfToken);
            final Traits traits = new Traits("dummy", null, null, language, "dummy", theme);
            this.kratosService.updateTraits(UUID.fromString(userId), traits);
        }
        final Cookie cookie = new Cookie("HANGAR_theme", theme);
        cookie.setPath("/");
        cookie.setMaxAge((int) (60 * 60 * 24 * 356.24 * 1000));
        // TODO make sure this cookie is cross hangar and auth
        response.addCookie(cookie);
    }
}
