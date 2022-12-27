package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.controller.model.Traits;
import io.papermc.hangarauth.service.KratosService;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
    public SettingsController(KratosService kratosService) {
        this.kratosService = kratosService;
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveSettings(@NotNull @PathVariable String userId,
                             @RequestParam(required = false) String flowId,
                             @RequestHeader(value = "cookie", required = false) String cookies,
                             @RequestParam(value = "csrf_token", required = false) String csrfToken,
                             @RequestParam String language, @RequestParam String theme, HttpServletResponse response) {
        if (!userId.equals("anon")) {
            this.kratosService.checkCsrfToken(flowId, cookies, csrfToken);
            Traits traits = new Traits("dummy", null, null, language, "dummy", theme);
            this.kratosService.updateTraits(UUID.fromString(userId), traits);
        }
        Cookie cookie = new Cookie("HANGAR_theme", theme);
        cookie.setPath("/");
        cookie.setMaxAge((int) (60 * 60 * 24 * 356.24 * 1000));
        // TODO make sure this cookie is cross hangar and auth
        response.addCookie(cookie);
    }
}
