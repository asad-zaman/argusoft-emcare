CREATE EXTENSION IF NOT EXISTS postgres_fdw;

CREATE SERVER IF NOT EXISTS emcare_db_fdw FOREIGN DATA WRAPPER postgres_fdw OPTIONS (host '127.0.0.1', port '5432', dbname 'emcare');

CREATE USER MAPPING IF NOT EXISTS FOR postgres SERVER emcare_db_fdw OPTIONS (user 'postgres', password 'argusadmin');

GRANT USAGE ON FOREIGN SERVER emcare_db_fdw TO postgres;

DO
$$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.foreign_tables
        WHERE foreign_table_schema = 'public'
        AND foreign_table_name IN ('user_entity', 'user_attribute')
    ) THEN
        IMPORT FOREIGN SCHEMA keycloak
        LIMIT TO (user_entity, user_attribute)
        FROM SERVER emcare_db_fdw INTO public;
    END IF;
END
$$;


