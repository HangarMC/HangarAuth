package io.papermc.hangarauth.db.model;

import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserAvatarTable extends AvatarTable {

    private final UUID identityId;

    @JdbiConstructor
    public UserAvatarTable(OffsetDateTime createdAt, UUID identityId, String hash, String fileName) {
        super(createdAt, hash, fileName);
        this.identityId = identityId;
    }

    public UserAvatarTable(UUID identityId, String hash, String fileName) {
        super(hash, fileName);
        this.identityId = identityId;
    }

    public UUID getIdentityId() {
        return identityId;
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
