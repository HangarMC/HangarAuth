package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("auth.hydra")
public record HydraConfig(@DefaultValue("http://localhost:4445") String adminUrl) {
}
