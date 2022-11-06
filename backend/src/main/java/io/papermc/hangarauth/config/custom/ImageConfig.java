package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties("auth.image")
public record ImageConfig(int interval, int threads, float quality, float qualityWebp, int size, List<String> whitelist) {
}
