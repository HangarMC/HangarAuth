package io.papermc.hangarauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("io.papermc.hangarauth.config.custom")
public class HangarAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangarAuthApplication.class, args);
    }

}
