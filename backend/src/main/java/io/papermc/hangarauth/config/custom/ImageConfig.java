package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.image")
public record ImageConfig(float quality, int size) {
}
