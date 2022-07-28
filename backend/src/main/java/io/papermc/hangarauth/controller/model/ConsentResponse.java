package io.papermc.hangarauth.controller.model;

import java.util.List;

public record ConsentResponse(
    String redirectTo,
    String challenge,
    String csrfToken,
    String username,
    String clientName,
    List<String> requestScope,
    String policyUri,
    String tosUri
) {
    public ConsentResponse(String redirectTo) {
        this(redirectTo, null, null, null, null, null, null, null);
    }
}
