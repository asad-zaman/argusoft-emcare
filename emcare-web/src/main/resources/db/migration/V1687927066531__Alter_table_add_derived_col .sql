alter table condition_resource
add column IF NOT EXISTS code text generated always as ((((((text::json ->>'code')::json)->>'coding')::json->>0)::json ->>'code')::text) stored;

alter table condition_resource
add column IF NOT EXISTS verification_status text generated always as ((((((text::json ->>'verificationStatus')::json)->>'coding')::json->>0)::json ->>'code')::text) stored;
