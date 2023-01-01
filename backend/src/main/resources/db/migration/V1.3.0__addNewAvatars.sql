CREATE TABLE avatars
(
    subject          varchar(255)             NOT NULL,
    type             varchar(16)              NOT NULL,
    optimized_hash   varchar(32)              NOT NULL,
    unoptimized_hash varchar(32)              NOT NULL,
    created_at       timestamp WITH TIME ZONE NOT NULL,
    version          int                      NOT NULL,
    CONSTRAINT avatars_pk
        PRIMARY KEY (subject, type)
);
