package io.papermc.hangarauth;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ConfigurationPropertiesScan("io.papermc.hangarauth.config.custom")
@ImportRuntimeHints(HangarAuthApplication.HangarAuthRuntimeHints.class)
public class HangarAuthApplication {

    public static void main(final String[] args) {
        SpringApplication.run(HangarAuthApplication.class, args);
    }

    static class HangarAuthRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
            hints.resources().registerResourceBundle("jakarta.servlet.LocalStrings");
            hints.resources().registerResourceBundle("jakarta.servlet.http.LocalStrings");
        }
    }
}
