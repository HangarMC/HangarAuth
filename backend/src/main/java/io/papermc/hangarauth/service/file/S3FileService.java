package io.papermc.hangarauth.service.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import io.papermc.hangarauth.config.custom.StorageConfig;

@Service
@ConditionalOnProperty(value = "auth.storage.type", havingValue = "object")
public class S3FileService implements FileService {

    private final StorageConfig config;
    private final ResourceLoader resourceLoader;
    private final S3Template s3Template;

    public S3FileService(StorageConfig config, ResourceLoader resourceLoader, S3Template s3Template) {
        this.config = config;
        this.resourceLoader = resourceLoader;
        this.s3Template = s3Template;
    }

    @Override
    public Resource getResource(String path) {
        return this.resourceLoader.getResource(path);
    }

    @Override
    public boolean exists(String path) {
        return getResource(path).exists();
    }

    @Override
    public void deleteDirectory(String dir) {
        this.s3Template.deleteObject(config.getBucket(), dir);
    }

    @Override
    public boolean delete(String path) {
        this.s3Template.deleteObject(path);
        return true;
    }

    @Override
    public byte[] bytes(String path) throws IOException {
        return getResource(path).getInputStream().readAllBytes();
    }

    @Override
    public void write(InputStream inputStream, String path, String contentType) throws IOException {
        // TODO need to somehow set the content type here
        S3Resource resource = (S3Resource) getResource(path);
        resource.setObjectMetadata(ObjectMetadata.builder().contentType(contentType).build());
        try (OutputStream outputStream = resource.getOutputStream()) {
            outputStream.write(inputStream.readAllBytes());
        }
    }

    @Override
    public String resolve(String path, String fileName) {
        if (path.endsWith("/")) {
            return path + fileName;
        } else {
            return path + "/" + fileName;
        }
    }

    @Override
    public String getRoot() {
        return "s3://" + config.getBucket() + "/";
    }

    @Override
    public String getDownloadUrl(String path) {
        return config.getCdnEndpoint() + "/" + config.getBucket() + "/" + path.replace("s3://" + config.getBucket() + "/", "");
    }
}
