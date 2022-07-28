package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.hydra")
public class HydraConfig {

    private String adminUrl = "http://localhost:4445";

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }
}
