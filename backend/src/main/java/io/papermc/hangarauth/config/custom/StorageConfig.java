package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.URISyntaxException;

import io.awspring.cloud.autoconfigure.core.AwsProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@ConfigurationProperties("auth.storage")
public record StorageConfig(
    @DefaultValue("local") String type,
    @DefaultValue("backend/work") String workDir,
    String accessKey,
    String secretKey,
    String bucket,
    String objectStorageEndpoint,
    String cdnEndpoint,
    @DefaultValue("true") boolean cdnIncludeBucket
) {

    @Component
    static final class AWS {

        private final StorageConfig config;

        AWS(StorageConfig config) {
            this.config = config;
        }

        @Bean
        public StaticCredentialsProvider credProvider() {
            return StaticCredentialsProvider.create(AwsBasicCredentials.create(this.config.accessKey(), this.config.secretKey()));
        }

        @Bean
        public AwsRegionProvider regionProvider() {
            return () -> Region.of("hangar");
        }

        @Bean
        public AwsProperties awsProperties() throws URISyntaxException {
            AwsProperties awsProperties = new AwsProperties();
            awsProperties.setEndpoint(new URI(this.config.objectStorageEndpoint()));
            return awsProperties;
        }

    }
}
