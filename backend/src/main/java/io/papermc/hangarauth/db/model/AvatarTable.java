package io.papermc.hangarauth.db.model;

import java.time.OffsetDateTime;

public class AvatarTable extends Table {

    protected String hash;
    protected String fileName;

    public AvatarTable(final OffsetDateTime createdAt, final String hash, final String fileName) {
        super(createdAt);
        this.hash = hash;
        this.fileName = fileName;
    }

    public AvatarTable(final String hash, final String fileName) {
        this.hash = hash;
        this.fileName = fileName;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(final String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
}
