package io.papermc.hangarauth.config.custom;

import io.papermc.hangarauth.AuthApiApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth")
public class GeneralConfig {

    private String dataDir = new ApplicationHome(AuthApiApplication.class).getDir().toPath().resolve("work").toString();
    private String publicHost = "http://localhost:3001";

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getPublicHost() {
        return publicHost;
    }

    public void setPublicHost(String publicHost) {
        this.publicHost = publicHost;
    }
}
