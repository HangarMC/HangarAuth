package io.papermc.hangarauth.db.model;

import java.time.OffsetDateTime;

public abstract class Table {

    private final OffsetDateTime createdAt;

    protected Table(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    protected Table() {
        this(null);
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Table{" +
            "createdAt=" + createdAt +
            '}';
    }
}
