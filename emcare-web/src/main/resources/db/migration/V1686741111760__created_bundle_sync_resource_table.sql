DROP TABLE IF EXISTS public.bundle_sync_resource;

CREATE TABLE IF NOT EXISTS public.bundle_sync_resource
(
    id bigint NOT NULL,
    synced_on timestamp without time zone NOT NULL,
    bundle_text text COLLATE pg_catalog."default",
    user_id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT bundle_sync_resource_pkey PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.bundle_sync_resource
    OWNER to postgres;