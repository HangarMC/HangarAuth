package io.papermc.hangarauth.db.model;

import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;

import java.time.OffsetDateTime;

public class OrgAvatarTable extends AvatarTable {

    private final String orgName;

    @JdbiConstructor
    public OrgAvatarTable(OffsetDateTime createdAt, String orgName, String hash, String fileName) {
        super(createdAt, hash, fileName);
        this.orgName = orgName;
        this.hash = hash;
        this.fileName = fileName;
    }

    public OrgAvatarTable(String orgName, String hash, String fileName) {
        super(hash, fileName);
        this.orgName = orgName;
        this.hash = hash;
        this.fileName = fileName;
    }

    public String getOrgName() {
        return orgName;
    }

    @Override
    public String toString() {
        return "OrgAvatarTable{" +
               "orgName=" + orgName +
               ", hash='" + hash + '\'' +
               ", fileName='" + fileName + '\'' +
               "} " + super.toString();
    }
}