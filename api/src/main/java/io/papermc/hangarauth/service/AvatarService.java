package io.papermc.hangarauth.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.imageio.ImageIO;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.db.dao.AvatarDAO;
import io.papermc.hangarauth.db.dao.OrgAvatarDAO;
import io.papermc.hangarauth.db.model.AvatarTable;
import io.papermc.hangarauth.db.model.OrgAvatarTable;
import io.papermc.hangarauth.db.model.UserAvatarTable;
import io.papermc.hangarauth.utils.Crypto;

@Service
public class AvatarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarDAO avatarDAO;
    private final OrgAvatarDAO orgAvatarDAO;
    private final Path avatarDir;
    private final ImageService imageService;

    @Autowired
    public AvatarService(AvatarDAO avatarDAO, OrgAvatarDAO orgAvatarDAO, GeneralConfig config, ImageService imageService) throws IOException {
        this.avatarDAO = avatarDAO;
        this.orgAvatarDAO = orgAvatarDAO;
        this.avatarDir = config.getDataDir().resolve("avatars");
        this.imageService = imageService;
        Files.createDirectories(this.avatarDir);
        LOGGER.info("Avatars directory: {}", avatarDir.toAbsolutePath());
    }

    public @NotNull Path getAvatarFor(@NotNull String folder, @NotNull String fileName) {
        return this.avatarDir.resolve(folder).resolve(fileName);
    }

    public @Nullable UserAvatarTable getUsersAvatarTable(@NotNull UUID userId) {
        return this.avatarDAO.getUserAvatar(userId);
    }

    public @Nullable OrgAvatarTable getOrgAvatarTable(@NotNull String orgName) {
        return this.orgAvatarDAO.getOrgAvatar(orgName);
    }

    public void deleteAvatarTable(@NotNull UUID userId) {
        this.avatarDAO.deleteUserAvatar(userId);
    }

    public void deleteAvatarTable(@NotNull String orgName) {
        this.orgAvatarDAO.deleteOrgAvatar(orgName);
    }

    @Transactional
    public void saveAvatar(@NotNull UUID userId, @NotNull MultipartFile avatar) throws IOException {
        checkAvatarFile(avatar);
        updateAvatar(userId, avatar, this.avatarDAO::getUserAvatar, this.avatarDAO::updateUserAvatar, this.avatarDAO::createUserAvatar, UserAvatarTable::new);
    }

    @Transactional
    public void saveOrgAvatar(@NotNull String orgName, @NotNull MultipartFile avatar) throws IOException {
        checkAvatarFile(avatar);
        updateAvatar(orgName, avatar, this.orgAvatarDAO::getOrgAvatar, this.orgAvatarDAO::updateOrgAvatar, this.orgAvatarDAO::createOrgAvatar, OrgAvatarTable::new);
    }

    private <T extends AvatarTable, S> void updateAvatar(S subject, MultipartFile avatar, Function<S, T> getter, Consumer<T> updater, Consumer<T> creator, TriFunction<S, String, String, T> ctor) throws IOException {
        String fileName = avatar.getOriginalFilename();
        if (fileName == null || "blob".equals(fileName)) {
            fileName = "blob.jpeg";
        }

        T table = getter.apply(subject);
        Consumer<T> createOrUpdate;
        if (table != null) {
            final String newHash = Crypto.md5ToHex(avatar.getBytes());
            if (table.getHash().equals(newHash)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't upload the same image");
            }
            table.setFileName(fileName);
            table.setHash(newHash);
            createOrUpdate = updater;
        } else {
            table = ctor.apply(subject, Crypto.md5ToHex(avatar.getBytes()), fileName);
            createOrUpdate = creator;
        }
        Path path = copyFileTo(subject.toString(), avatar, fileName);
        imageService.evictCache(path.toString());
        createOrUpdate.accept(table);
    }

    private void checkAvatarFile(@NotNull MultipartFile avatar) {
        if (!StringUtils.equalsAny(avatar.getContentType(), MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid file type, only png or jpeg");
        }
        if (StringUtils.isBlank(avatar.getOriginalFilename()) || avatar.getOriginalFilename().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid filename");
        }
    }

    private Path copyFileTo(@NotNull String subject, @NotNull MultipartFile avatar, String fileName) throws IOException {
        final Path subjectDir = this.avatarDir.resolve(subject);
        Files.createDirectories(subjectDir);
        FileUtils.cleanDirectory(subjectDir.toFile());
        final Path file = subjectDir.resolve(fileName);

        // convert everything to jpeg cause they compress better
        if (!MediaType.IMAGE_JPEG_VALUE.equals(avatar.getContentType())) {
            BufferedImage img = ImageIO.read(avatar.getInputStream());
            // draw to get rid of alpha
            BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            result.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
            boolean dum = ImageIO.write(result, "jpg", file.toFile());
            if (!dum) System.out.println("failed to write jpg " + file.toFile());
        } else {
            Files.copy(avatar.getInputStream(), file);
        }

        return file;
    }
}
