SELECT current_database(), version();
SELECT version, description, success FROM public.flyway_schema_history ORDER BY installed_rank;
SELECT column_name, data_type, is_nullable FROM information_schema.columns
WHERE table_schema='public' AND table_name='models' ORDER BY ordinal_position;
SELECT m.* FROM public.models m JOIN public.tenants t ON t.id=m.tenant_id WHERE t.slug='databasefirst' ORDER BY m.id;
