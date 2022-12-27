package io.papermc.hangarauth.config.custom;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.image")
public record ImageConfig(int interval, int threads, float quality, float qualityWebp, int size, List<String> whitelist) {
}
