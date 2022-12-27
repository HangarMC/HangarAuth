package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("auth.invite")
public record InviteConfig(
    String apiKey,
    @DefaultValue("false") boolean enabled
) {
}
