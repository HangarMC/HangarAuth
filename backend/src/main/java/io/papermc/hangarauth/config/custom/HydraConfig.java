package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("auth.hydra")
public record HydraConfig(@DefaultValue("http://localhost:4445") String adminUrl) {
}
