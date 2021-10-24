package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.custom.FilesConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.UUID;

@Service
public class AvatarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    private final Path dataDir;
    private final Path avatarDir;

    @Autowired
    public AvatarService(FilesConfig config) {
        this.dataDir = Path.of(config.getDataDir());
        this.avatarDir = this.dataDir.resolve("avatars");
        LOGGER.info("Avatars directory: {}", avatarDir.toAbsolutePath());
    }

    public @NotNull Path getAvatarFor(@NotNull UUID userId, @NotNull String fileName) {
        return this.avatarDir.resolve(userId.toString()).resolve(fileName);
    }
}
