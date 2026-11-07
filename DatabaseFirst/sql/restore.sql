DO $$ BEGIN
 IF current_database() <> 'estoque_databasefirst' THEN
  RAISE EXCEPTION 'Use somente o banco estoque_databasefirst';
 END IF;
END $$;
ALTER TABLE public.models DROP COLUMN IF EXISTS demo_note;
DELETE FROM public.models WHERE tenant_id = (SELECT id FROM tenants WHERE slug='databasefirst');
DELETE FROM public.audit WHERE tenant_id = (SELECT id FROM tenants WHERE slug='databasefirst');
