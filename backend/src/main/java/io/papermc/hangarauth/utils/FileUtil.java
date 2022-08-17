package io.papermc.hangarauth.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    public static void createDirectories(final Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
    }
}
