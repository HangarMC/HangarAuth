package io.papermc.hangarauth.db.dao;

import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KratosIdentityDAO {

    @SqlQuery("SELECT ic.identity_id " +
              "FROM public.identity_credential_identifiers ici " +
              "JOIN public.identity_credentials ic ON ici.identity_credential_id = ic.id " +
              "WHERE ici.identifier = lower(:identifier)")
    UUID getUserId(String identifier);
}
