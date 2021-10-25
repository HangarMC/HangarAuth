package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.db.dao.AvatarDAO;
import io.papermc.hangarauth.db.model.UserAvatarTable;
import io.papermc.hangarauth.utils.Crypto;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class AvatarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarDAO avatarDAO;
    private final Path dataDir;
    private final Path avatarDir;

    @Autowired
    public AvatarService(AvatarDAO avatarDAO, GeneralConfig config) throws IOException {
        this.avatarDAO = avatarDAO;
        this.dataDir = Path.of(config.getDataDir());
        this.avatarDir = this.dataDir.resolve("avatars");
        Files.createDirectories(this.avatarDir);
        LOGGER.info("Avatars directory: {}", avatarDir.toAbsolutePath());
    }

    public @NotNull Path getAvatarFor(@NotNull UUID userId, @NotNull String fileName) {
        return this.avatarDir.resolve(userId.toString()).resolve(fileName);
    }

    public @Nullable UserAvatarTable getUsersAvatarTable(@NotNull UUID userId) {
        return this.avatarDAO.getUserAvatar(userId);
    }

    @Transactional
    public void saveAvatar(@NotNull UUID userId, @NotNull MultipartFile avatar) throws IOException {
        if (!StringUtils.equalsAny(avatar.getContentType(), MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid file type, only png or jpeg");
        }
        if (StringUtils.isBlank(avatar.getOriginalFilename()) || avatar.getOriginalFilename().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid filename");
        }
        UserAvatarTable table = this.avatarDAO.getUserAvatar(userId);
        Consumer<UserAvatarTable> createOrUpdate;
        if (table != null) {
            final String newHash = Crypto.md5ToHex(avatar.getBytes());
            if (table.getHash().equals(newHash)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't upload the same image");
            }
            table.setFileName(avatar.getOriginalFilename());
            table.setHash(newHash);
            createOrUpdate = this.avatarDAO::updateUserAvatar;
        } else {
            table = new UserAvatarTable(userId, Crypto.md5ToHex(avatar.getBytes()), avatar.getOriginalFilename());
            createOrUpdate = this.avatarDAO::createUserAvatar;
        }
        copyFileTo(userId, avatar);
        createOrUpdate.accept(table);
    }

    private void copyFileTo(@NotNull UUID userId, @NotNull MultipartFile avatar) throws IOException {
        final Path userAvatarDir = this.avatarDir.resolve(userId.toString());
        Files.createDirectories(userAvatarDir);
        FileUtils.cleanDirectory(userAvatarDir.toFile());
        Files.copy(avatar.getInputStream(), userAvatarDir.resolve(avatar.getOriginalFilename()));
    }
}
