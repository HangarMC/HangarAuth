package io.papermc.hangarauth.db.model;

import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserAvatarTable extends Table {

    private final UUID identityId;
    private final String hash;
    private final String fileName;

    @JdbiConstructor
    public UserAvatarTable(OffsetDateTime createdAt, UUID identityId, String hash, String fileName) {
        super(createdAt);
        this.identityId = identityId;
        this.hash = hash;
        this.fileName = fileName;
    }

    public UUID getIdentityId() {
        return identityId;
    }

    public String getHash() {
        return hash;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "UserAvatarTable{" +
            "identityId=" + identityId +
            ", hash='" + hash + '\'' +
            ", fileName='" + fileName + '\'' +
            "} " + super.toString();
    }
}
