package io.papermc.hangarauth.controller;

import io.papermc.hangarauth.config.custom.InviteConfig;
import io.papermc.hangarauth.controller.model.InviteHookData;
import io.papermc.hangarauth.service.InviteService;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/invite")
public class InviteController {

    private final InviteService service;
    private final InviteConfig config;

    @Autowired
    public InviteController(final InviteService service, final InviteConfig config) {
        this.service = service;
        this.config = config;
    }

    @PostMapping("/use")
    public ResponseEntity<Object> useInvite(@RequestBody final @NotNull InviteHookData body, @RequestHeader("X-Kratos-Hook-Api-Key") final String apiKey) {
        if (!apiKey.equals(this.config.apiKey())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        final Map<String, Object> result = this.service.handleInvite(body);
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
