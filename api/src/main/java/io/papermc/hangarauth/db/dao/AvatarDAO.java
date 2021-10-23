package io.papermc.hangarauth.db.dao;

import io.papermc.hangarauth.db.model.UserAvatarTable;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RegisterConstructorMapper(UserAvatarTable.class)
public interface AvatarDAO {

    @SqlQuery("SELECT * FROM identity_avatars WHERE identity_id = :userId")
    UserAvatarTable getUserAvatar(UUID userId);
}
