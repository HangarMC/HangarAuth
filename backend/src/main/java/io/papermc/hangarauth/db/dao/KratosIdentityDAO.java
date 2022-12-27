package io.papermc.hangarauth.db.dao;

import java.util.UUID;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface KratosIdentityDAO {

    @SqlQuery("SELECT ic.identity_id " +
        "FROM public.identity_credential_identifiers ici " +
        "JOIN public.identity_credentials ic ON ici.identity_credential_id = ic.id " +
        "WHERE ici.identifier = lower(:identifier)")
    UUID getUserId(String identifier);
}
