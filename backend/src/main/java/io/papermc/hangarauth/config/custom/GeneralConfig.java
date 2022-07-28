package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties("auth")
public class GeneralConfig {

    private Path dataDir = Path.of("api", "work");
    private String publicHost = "http://localhost:3001";
    private String apiKey = "supersecret2";

    public Path getDataDir() {
        return dataDir;
    }

    public void setDataDir(Path dataDir) {
        this.dataDir = dataDir;
    }

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
}
