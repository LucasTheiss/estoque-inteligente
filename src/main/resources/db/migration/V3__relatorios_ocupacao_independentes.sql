ALTER TABLE tenants RENAME COLUMN enterprise TO reports;
ALTER TABLE tenants ADD COLUMN occupancy BOOLEAN NOT NULL DEFAULT false;

-- Preserva os recursos das lojas existentes antes de permitir escolhas independentes.
UPDATE tenants SET occupancy = reports;
