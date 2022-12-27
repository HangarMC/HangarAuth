CREATE TABLE invites
(
    id varchar(32) NOT NULL PRIMARY KEY
);

CREATE TABLE invites_identities
(
    invite_id   varchar(32) NOT NULL
        CONSTRAINT invites_identities_invite_id_fkey REFERENCES invites
            ON DELETE CASCADE,
    identity_id uuid        NOT NULL UNIQUE
    /* CONSTRAINT invites_identities_identity_id_fkey REFERENCES public.identities ON DELETE CASCADE can't be a FK cause this is inserted before kratos inserts stuff */
);
