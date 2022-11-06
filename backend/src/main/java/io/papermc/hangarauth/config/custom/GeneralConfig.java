package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("auth")
public record GeneralConfig(
    @DefaultValue("http://localhost:3001") String publicHost,
    @DefaultValue("supersecret2") String apiKey,
    @DefaultValue("http://localhost:3333") String hangarFrontendHost,
    @DefaultValue("http://localhost:3333") String hangarBackendHost
) {
}
