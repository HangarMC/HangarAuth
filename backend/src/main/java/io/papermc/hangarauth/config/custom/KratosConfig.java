package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.kratos")
public class KratosConfig {

    private String adminUrl = "http://localhost:4434";
    private String publicUrl = "http://localhost:4433";
    private String publicBackendUrl = "http://localhost:4433";

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getPublicBackendUrl() {
        return publicBackendUrl;
    }

    public void setPublicBackendUrl(String publicBackendUrl) {
        this.publicBackendUrl = publicBackendUrl;
    }
}
