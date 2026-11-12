DO $$ BEGIN
 IF current_database() <> 'estoque_databasefirst' THEN
  RAISE EXCEPTION 'Use somente o banco estoque_databasefirst';
 END IF;
END $$;
ALTER TABLE public.models ADD COLUMN IF NOT EXISTS demo_note TEXT;
