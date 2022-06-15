package io.papermc.hangarauth.db.model;

import java.time.OffsetDateTime;

public class AvatarTable extends Table {

    protected String hash;
    protected String fileName;

    public AvatarTable(OffsetDateTime createdAt, String hash, String fileName) {
        super(createdAt);
        this.hash = hash;
        this.fileName = fileName;
    }

    public AvatarTable(String hash, String fileName) {
        this.hash = hash;
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
