package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("auth.kratos")
public record KratosConfig(
    @DefaultValue("http://localhost:4434") String adminUrl,
    @DefaultValue("http://localhost:4433") String publicUrl,
    @DefaultValue("http://localhost:4433") String publicBackendUrl
) {
}
