package io.papermc.hangarauth.db.dao;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.Timestamped;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

import io.papermc.hangarauth.db.model.OrgAvatarTable;

@Repository
@RegisterConstructorMapper(OrgAvatarTable.class)
public interface OrgAvatarDAO {

    @Timestamped
    @SqlUpdate("INSERT INTO org_avatars (org_name, created_at, hash, file_name) VALUES (:orgName, :now, :hash, :fileName)")
    void createOrgAvatar(@BindBean OrgAvatarTable userAvatarTable);

    @SqlQuery("SELECT * FROM org_avatars WHERE org_name = :orgName")
    OrgAvatarTable getOrgAvatar(String orgName);

    @Timestamped
    @SqlUpdate("UPDATE org_avatars SET hash = :hash, created_at = :now, file_name = :fileName WHERE org_name = :orgName")
    void updateOrgAvatar(@BindBean OrgAvatarTable userAvatarTable);

    @SqlUpdate("DELETE FROM org_avatars WHERE org_name = :orgName")
    void deleteOrgAvatar(String orgName);
}
