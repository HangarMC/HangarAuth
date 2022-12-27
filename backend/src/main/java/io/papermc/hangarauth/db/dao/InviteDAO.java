package io.papermc.hangarauth.db.dao;

import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteDAO {

    @SqlQuery("SELECT id FROM invites WHERE id = :id LIMIT 1")
    Optional<String> getInvite(String id);

    @SqlUpdate("INSERT INTO invites_identities (invite_id, identity_id) VALUES (:inviteId, :identityId)")
    void insertInviteUse(String inviteId, UUID identityId);
}
