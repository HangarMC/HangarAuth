package io.papermc.hangarauth.service.file;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.Resource;

public interface FileService {

    Resource getResource(String path);

    boolean exists(String path);

    void deleteDirectory(String dir);

    boolean delete(String path);

    byte[] bytes(String path) throws IOException;

    void write(InputStream inputStream, String path, String contentType) throws IOException;

    String resolve(String path, String fileName);

    String getRoot();

    String getDownloadUrl(String path);
}
