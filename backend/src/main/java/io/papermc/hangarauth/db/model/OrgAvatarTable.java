package io.papermc.hangarauth.db.model;

import java.time.OffsetDateTime;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

public class OrgAvatarTable extends AvatarTable {

    private final String orgName;

    @JdbiConstructor
    public OrgAvatarTable(final OffsetDateTime createdAt, final String orgName, final String hash, final String fileName) {
        super(createdAt, hash, fileName);
        this.orgName = orgName;
        this.hash = hash;
        this.fileName = fileName;
    }

    public OrgAvatarTable(final String orgName, final String hash, final String fileName) {
        super(hash, fileName);
        this.orgName = orgName;
        this.hash = hash;
        this.fileName = fileName;
    }

    public String getOrgName() {
        return this.orgName;
    }

    @Override
    public String toString() {
        return "OrgAvatarTable{" +
               "orgName=" + this.orgName +
               ", hash='" + this.hash + '\'' +
               ", fileName='" + this.fileName + '\'' +
               "} " + super.toString();
    }
}
