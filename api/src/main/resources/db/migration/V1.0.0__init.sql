CREATE TABLE identity_avatars
(
    identity_id uuid NOT NULL
        CONSTRAINT user_avatars_user_id_fkey REFERENCES public.identities ON DELETE CASCADE,
    created_at timestamp with time zone NOT NULL,
    hash varchar(32) NOT NULL,
    file_name varchar(255) NOT NULL
)
