package io.papermc.hangarauth.service.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.papermc.hangarauth.config.custom.StorageConfig;
import io.papermc.hangarauth.utils.FileUtils;

@Service
@ConditionalOnProperty(value = "auth.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageFileService implements FileService {

    private final StorageConfig config;

    public LocalStorageFileService(StorageConfig config) {
        this.config = config;
    }

    @Override
    public Resource getResource(String path) {
        return new FileSystemResource(path);
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(Path.of(path));
    }

    @Override
    public void deleteDirectory(String dir) {
        FileUtils.deleteDirectory(Path.of(dir));
    }

    @Override
    public boolean delete(String path) {
        return FileUtils.delete(Path.of(path));
    }

    @Override
    public byte[] bytes(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    @Override
    public void write(InputStream inputStream, String path) throws IOException {
        Path p = Path.of(path);
        if (Files.notExists(p)) {
            Files.createDirectories(p.getParent());
        }
        Files.copy(inputStream, p);
    }

    @Override
    public String resolve(String path, String fileName) {
        return Path.of(path).resolve(fileName).toString();
    }

    @Override
    public String getRoot() {
        return config.getWorkDir();
    }

    @Override
    public String getDownloadUrl(String folder, String fileName) {
        throw new UnsupportedOperationException();
    }
}