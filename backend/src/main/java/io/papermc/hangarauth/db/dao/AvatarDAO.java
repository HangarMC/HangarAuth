package io.papermc.hangarauth.db.dao;

import io.papermc.hangarauth.db.model.UserAvatarTable;
import java.util.UUID;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

@Repository
@RegisterConstructorMapper(UserAvatarTable.class)
public interface AvatarDAO {

    @Timestamped
    @SqlUpdate("INSERT INTO identity_avatars (identity_id, created_at, hash, file_name) VALUES (:identityId, :now, :hash, :fileName)")
    void createUserAvatar(@BindBean UserAvatarTable userAvatarTable);

    @SqlQuery("SELECT * FROM identity_avatars WHERE identity_id = :userId")
    UserAvatarTable getUserAvatar(UUID userId);

    @Timestamped
    @SqlUpdate("UPDATE identity_avatars SET hash = :hash, created_at = :now, file_name = :fileName WHERE identity_id = :identityId")
    void updateUserAvatar(@BindBean UserAvatarTable userAvatarTable);

    @SqlUpdate("DELETE FROM identity_avatars WHERE identity_id = :userId")
    void deleteUserAvatar(UUID userId);
}
