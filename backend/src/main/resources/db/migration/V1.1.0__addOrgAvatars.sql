CREATE TABLE org_avatars
(
    org_name   varchar(32)              NOT NULL,
    created_at timestamp WITH TIME ZONE NOT NULL,
    hash       varchar(32)              NOT NULL,
    file_name  varchar(255)             NOT NULL
)
