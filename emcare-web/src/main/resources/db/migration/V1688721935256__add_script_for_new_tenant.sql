alter table emcare_resources
add column IF NOT EXISTS birth_date text
GENERATED ALWAYS AS ((text)::json ->> 'birthDate'::text) STORED;

alter table condition_resource
add column IF NOT EXISTS d_patient_id text generated always as (((((text::json ->>'subject')::json)->>'identifier')::json->>'value')::text) stored;

alter table condition_resource
add column IF NOT EXISTS d_encounter_id text generated always as (((((text::json ->>'encounter')::json)->>'identifier')::json->>'value')::text) stored;

