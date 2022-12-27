package io.papermc.hangarauth.db.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class UserAvatarTable extends AvatarTable {

    private final UUID identityId;

    @JdbiConstructor
    public UserAvatarTable(final OffsetDateTime createdAt, final UUID identityId, final String hash, final String fileName) {
        super(createdAt, hash, fileName);
        this.identityId = identityId;
    }

    public UserAvatarTable(final UUID identityId, final String hash, final String fileName) {
        super(hash, fileName);
        this.identityId = identityId;
    }

    public UUID getIdentityId() {
        return this.identityId;
    }

    @Override
    public String toString() {
        return "UserAvatarTable{" +
            "identityId=" + this.identityId +
            ", hash='" + this.hash + '\'' +
            ", fileName='" + this.fileName + '\'' +
            "} " + super.toString();
    }
}
