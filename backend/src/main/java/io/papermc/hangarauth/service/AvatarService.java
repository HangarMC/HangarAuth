package io.papermc.hangarauth.service;

import io.papermc.hangarauth.config.custom.GeneralConfig;
import io.papermc.hangarauth.controller.AvatarController;
import io.papermc.hangarauth.db.dao.AvatarDAO;
import io.papermc.hangarauth.db.model.AvatarTable;
import io.papermc.hangarauth.service.file.FileService;
import io.papermc.hangarauth.service.file.S3FileService;
import io.papermc.hangarauth.utils.Crypto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AvatarService {

    private final GeneralConfig generalConfig;
    private final FileService fileService;
    private final AvatarDAO avatarDAO;

    private final String defaultAvatarUrl;
    private final String defaultAvatarPath;

    public AvatarService(final GeneralConfig generalConfig, final FileService fileService, final AvatarDAO avatarDAO) {
        this.generalConfig = generalConfig;
        this.fileService = fileService;
        this.avatarDAO = avatarDAO;

        // setup default
        this.defaultAvatarPath = this.fileService.resolve(this.fileService.getRoot(), "/avatars/default.webp");
        if (!this.fileService.exists(this.defaultAvatarPath)) {
            try {
                this.fileService.write(AvatarService.class.getClassLoader().getResourceAsStream("avatar/default.webp"), this.defaultAvatarPath, AvatarController.WEBP.toString());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (this.fileService instanceof S3FileService s3) {
            this.defaultAvatarUrl = s3.getDownloadUrl(this.defaultAvatarPath);
        } else {
            this.defaultAvatarUrl = this.generalConfig.publicHost() + "/avatar-new/default/default.webp";
        }
    }

    public String getAvatarUrl(final String type, final String subject) {
        if (type.equals("default") && subject.equals("default")) {
            return this.defaultAvatarUrl;
        }
        this.checkAndMigrate(type, subject);
        final AvatarTable table = this.avatarDAO.getAvatar(type, subject);
        if (table == null) {
            return this.defaultAvatarUrl;
        } else {
            return this.getCdnPath(type, subject, table.getVersion());
        }
    }

    public byte[] getAvatar(final String type, final String subject) throws IOException {
        if (type.equals("default") && subject.equals("default")) {
            return this.fileService.bytes(this.defaultAvatarPath);
        }
        this.checkAndMigrate(type, subject);
        final AvatarTable table = this.avatarDAO.getAvatar(type, subject);
        if (table == null) {
            return this.fileService.bytes(this.defaultAvatarPath);
        } else {
            return this.fileService.bytes(this.getPath(type, subject));
        }
    }

    public void setAvatar(final String type, final String subject, byte[] avatar) throws IOException {
        final String unoptimizedHash = Crypto.md5ToHex(avatar);
        AvatarTable table = this.avatarDAO.getAvatar(type, subject);
        if (table != null && table.getUnoptimizedHash().equals(unoptimizedHash)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't upload the same avatar again!");
        }
        avatar = this.convertAndOptimize(avatar);
        final String optimizedHash = Crypto.md5ToHex(avatar);
        if (table == null) {
            table = new AvatarTable(type, subject, optimizedHash, unoptimizedHash, 1);
            this.avatarDAO.createAvatar(table);
        } else {
            table.setOptimizedHash(optimizedHash);
            table.setUnoptimizedHash(unoptimizedHash);
            table.setVersion(table.getVersion() + 1);
            this.avatarDAO.updateAvatar(table);
        }
        this.fileService.write(new ByteArrayInputStream(avatar), this.getPath(type, subject), AvatarController.WEBP.toString());
    }

    public void deleteAvatar(final String type, final String subject) {
        this.avatarDAO.deleteAvatar(type, subject);
        this.fileService.delete(this.getPath(type, subject));
    }

    private String getPath(final String type, final String subject) {
        return this.fileService.resolve(this.fileService.getRoot(), "/avatars/" + type + "/" + subject + ".webp");
    }

    private String getCdnPath(final String type, final String subject, final int version) {
        if (this.fileService instanceof S3FileService s3) {
            return s3.getDownloadUrl(this.getPath(type, subject)) + "?v=" + version;
        } else {
            return this.generalConfig.publicHost() + "/avatar-new/" + type + "/" + subject + ".webp?v=" + version;
        }
    }

    private byte[] convertAndOptimize(final byte[] avatar) {
        // TODO convert to webp
        // TODO optimize
        return avatar;
    }

    private void checkAndMigrate(final String type, final String subject) {
        // todo check old table
        // todo migrate to new table and upload (just call setAvatar)
    }
}
