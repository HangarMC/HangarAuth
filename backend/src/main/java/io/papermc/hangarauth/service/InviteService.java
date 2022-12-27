package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.custom.InviteConfig;
import io.papermc.hangarauth.controller.model.InviteHookData;
import io.papermc.hangarauth.db.dao.InviteDAO;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class InviteService {

    private final InviteDAO dao;
    private final InviteConfig config;

    @Autowired
    public InviteService(InviteDAO dao, InviteConfig config) {
        this.dao = dao;
        this.config = config;
    }

    public Map<String, Object> handleInvite(InviteHookData data) {
        try {
            // get invite from url
            String invite = UriComponentsBuilder.fromUriString(data.url()).build().getQueryParams().getFirst("invite");
            // check that invite is valid
            if (invite == null) {
                if (config.enabled()) {
                    return errorPayload("Signup is invite only right now, sorry!");
                }
                return Map.of();
            }
            if (dao.getInvite(invite).isEmpty()) {
                return errorPayload("Unknown invite " + invite + ". Please contact whoever send you this link.");
            }
            // save invite user combo into db
            dao.insertInviteUse(invite, data.id());
            return Map.of();
        } catch (Exception ex) {
            return errorPayload("Error while checking invite. Please contact an admin (" + ex.getMessage() + ")");
        }
    }

    private Map<String, Object> errorPayload(String message) {
        return Map.of("messages", List.of(Map.of(
                "instance_ptr", "#/method",
                "messages", List.of(
                        Map.of("id", 123,
                                "text", message,
                                "type", "error")
                ))));
    }
}
