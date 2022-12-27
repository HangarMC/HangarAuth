package io.papermc.hangarauth.service.file;

import io.papermc.hangarauth.config.custom.StorageConfig;
import io.papermc.hangarauth.utils.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@ConditionalOnProperty(value = "auth.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageFileService implements FileService {

    private final StorageConfig config;

    public LocalStorageFileService(final StorageConfig config) {
        this.config = config;
    }

    @Override
    public Resource getResource(final String path) {
        return new FileSystemResource(path);
    }

    @Override
    public boolean exists(final String path) {
        return Files.exists(Path.of(path));
    }

    @Override
    public void deleteDirectory(final String dir) {
        FileUtils.deleteDirectory(Path.of(dir));
    }

    @Override
    public boolean delete(final String path) {
        return FileUtils.delete(Path.of(path));
    }

    @Override
    public byte[] bytes(final String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    @Override
    public void write(final InputStream inputStream, final String path, final String contentType) throws IOException {
        final Path p = Path.of(path);
        if (Files.notExists(p)) {
            Files.createDirectories(p.getParent());
        }
        Files.copy(inputStream, p);
    }

    @Override
    public String resolve(final String path, final String fileName) {
        return Path.of(path).resolve(fileName).toString();
    }

    @Override
    public String getRoot() {
        return this.config.workDir();
    }

    @Override
    public String getDownloadUrl(final String path) {
        throw new UnsupportedOperationException();
    }
}
