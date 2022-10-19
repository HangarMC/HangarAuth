package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth")
public class GeneralConfig {

    private String publicHost = "http://localhost:3001";
    private String apiKey = "supersecret2";
    private String hangarFrontendHost = "http://localhost:3333";
    private String hangarBackendHost = "http://localhost:3333";

    public String getPublicHost() {
        return publicHost;
    }

    public void setPublicHost(String publicHost) {
        this.publicHost = publicHost;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getHangarFrontendHost() {
        return hangarFrontendHost;
    }

    public void setHangarFrontendHost(String hangarFrontendHost) {
        this.hangarFrontendHost = hangarFrontendHost;
    }

    public String getHangarBackendHost() {
        return hangarBackendHost;
    }

    public void setHangarBackendHost(String hangarBackendHost) {
        this.hangarBackendHost = hangarBackendHost;
    }
}
