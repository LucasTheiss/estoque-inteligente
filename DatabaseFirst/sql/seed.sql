DO $$ BEGIN
 IF current_database() <> 'estoque_databasefirst' THEN
  RAISE EXCEPTION 'Use somente o banco estoque_databasefirst';
 END IF;
END $$;
INSERT INTO tenants(slug,name) VALUES ('databasefirst','Loja exclusiva Database First')
ON CONFLICT(slug) DO NOTHING;
