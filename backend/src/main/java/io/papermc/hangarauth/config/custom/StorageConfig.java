package io.papermc.hangarauth.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

import io.awspring.cloud.autoconfigure.core.AwsProperties;
import io.papermc.hangarauth.HangarAuthApplication;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@Configuration
@ConfigurationProperties("auth.storage")
public class StorageConfig {

    // type = local or object
    private String type = "local";
    // local
    private String workDir = new ApplicationHome(HangarAuthApplication.class).getDir().toPath().resolve("work").toString();
    // object
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String objectStorageEndpoint;
    private String cdnEndpoint;

    @Bean
    public StaticCredentialsProvider credProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(getAccessKey(), getSecretKey()));
    }

    @Bean
    public AwsRegionProvider regionProvider() {
        return () -> Region.of("hangar");
    }

    @Bean
    public AwsProperties awsProperties() throws URISyntaxException {
        AwsProperties awsProperties = new AwsProperties();
        awsProperties.setEndpoint(new URI(objectStorageEndpoint));
        return awsProperties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getObjectStorageEndpoint() {
        return objectStorageEndpoint;
    }

    public void setObjectStorageEndpoint(String objectStorageEndpoint) {
        this.objectStorageEndpoint = objectStorageEndpoint;
    }

    public String getCdnEndpoint() {
        return cdnEndpoint;
    }

    public void setCdnEndpoint(String cdnEndpoint) {
        this.cdnEndpoint = cdnEndpoint;
    }
}
