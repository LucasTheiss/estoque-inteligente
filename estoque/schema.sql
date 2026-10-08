CREATE TABLE IF NOT EXISTS tenants (
 id BIGSERIAL PRIMARY KEY, slug TEXT NOT NULL UNIQUE, name TEXT NOT NULL,
 iot BOOLEAN NOT NULL DEFAULT false, enterprise BOOLEAN NOT NULL DEFAULT false,
 fractional BOOLEAN NOT NULL DEFAULT false,
 mqtt_host TEXT NOT NULL DEFAULT 'mqtt', mqtt_port INT NOT NULL DEFAULT 1883 CHECK(mqtt_port BETWEEN 1 AND 65535),
 mqtt_qos INT NOT NULL DEFAULT 1 CHECK(mqtt_qos BETWEEN 0 AND 1),
 led_seconds INT NOT NULL DEFAULT 30 CHECK(led_seconds BETWEEN 5 AND 60)
);
CREATE TABLE IF NOT EXISTS users (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 username TEXT NOT NULL, password_hash TEXT NOT NULL,
 role TEXT NOT NULL CHECK(role IN ('vendedor','estoquista','gerente','admin')),
 active BOOLEAN NOT NULL DEFAULT true, auth_version INT NOT NULL DEFAULT 1,
 UNIQUE(tenant_id, username), UNIQUE(tenant_id,id)
);
CREATE TABLE IF NOT EXISTS role_permissions (
 tenant_id BIGINT NOT NULL REFERENCES tenants, role TEXT NOT NULL,
 permission TEXT NOT NULL, PRIMARY KEY(tenant_id,role,permission)
);
CREATE TABLE IF NOT EXISTS models (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 name TEXT NOT NULL, brand TEXT NOT NULL DEFAULT '', category TEXT NOT NULL DEFAULT '',
 UNIQUE(tenant_id,id), UNIQUE(tenant_id,name,brand,category)
);
CREATE TABLE IF NOT EXISTS products (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 model_id BIGINT NOT NULL, sku TEXT NOT NULL, barcode TEXT NOT NULL DEFAULT '',
 color TEXT NOT NULL DEFAULT '', size TEXT NOT NULL DEFAULT '',
 unit TEXT NOT NULL CHECK(unit IN ('un','kg')), price NUMERIC(18,2) NOT NULL CHECK(price>=0),
 attributes JSONB NOT NULL DEFAULT '{}', active BOOLEAN NOT NULL DEFAULT true,
 UNIQUE(tenant_id,sku), UNIQUE(tenant_id,id),
 FOREIGN KEY(tenant_id,model_id) REFERENCES models(tenant_id,id)
);
CREATE UNIQUE INDEX IF NOT EXISTS product_barcode ON products(tenant_id,barcode) WHERE barcode<>'' AND active;
CREATE TABLE IF NOT EXISTS positions (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 code TEXT NOT NULL, sector TEXT NOT NULL, row_no INT NOT NULL DEFAULT 0 CHECK(row_no>=0),
 col_no INT NOT NULL DEFAULT 0 CHECK(col_no>=0), capacity NUMERIC(18,3) NOT NULL CHECK(capacity>0),
 unit TEXT NOT NULL CHECK(unit IN ('un','kg')), device TEXT NOT NULL DEFAULT '',
 pin INT CHECK(pin BETWEEN 0 AND 39), active BOOLEAN NOT NULL DEFAULT true,
 UNIQUE(tenant_id,code), UNIQUE(tenant_id,id)
);
CREATE TABLE IF NOT EXISTS occupancy (
 tenant_id BIGINT NOT NULL, product_id BIGINT NOT NULL, position_id BIGINT NOT NULL,
 quantity NUMERIC(18,3) NOT NULL DEFAULT 0 CHECK(quantity>=0),
 PRIMARY KEY(tenant_id,product_id,position_id),
 FOREIGN KEY(tenant_id,product_id) REFERENCES products(tenant_id,id),
 FOREIGN KEY(tenant_id,position_id) REFERENCES positions(tenant_id,id)
);
CREATE TABLE IF NOT EXISTS movements (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 product_id BIGINT NOT NULL, position_id BIGINT NOT NULL, actor_id BIGINT NOT NULL,
 kind TEXT NOT NULL CHECK(kind IN ('entrada','saida','ajuste','estorno')),
 delta NUMERIC(18,3) NOT NULL CHECK(delta<>0), before_qty NUMERIC(18,3) NOT NULL CHECK(before_qty>=0),
 after_qty NUMERIC(18,3) NOT NULL CHECK(after_qty>=0),
 reason TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 request_key TEXT NOT NULL, fingerprint TEXT NOT NULL, reverse_of BIGINT,
 UNIQUE(tenant_id,request_key), UNIQUE(tenant_id,id), UNIQUE(tenant_id,reverse_of),
 FOREIGN KEY(tenant_id,product_id) REFERENCES products(tenant_id,id),
 FOREIGN KEY(tenant_id,position_id) REFERENCES positions(tenant_id,id),
 FOREIGN KEY(tenant_id,actor_id) REFERENCES users(tenant_id,id),
 FOREIGN KEY(tenant_id,reverse_of) REFERENCES movements(tenant_id,id),
 CHECK(after_qty=before_qty+delta)
);
CREATE INDEX IF NOT EXISTS movement_history ON movements(tenant_id,created_at DESC);
CREATE TABLE IF NOT EXISTS audit (
 id BIGSERIAL PRIMARY KEY, tenant_id BIGINT NOT NULL REFERENCES tenants,
 actor_id BIGINT, action TEXT NOT NULL, detail JSONB NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 FOREIGN KEY(tenant_id,actor_id) REFERENCES users(tenant_id,id)
);
CREATE TABLE IF NOT EXISTS login_attempts (
 key TEXT PRIMARY KEY, attempts INT NOT NULL DEFAULT 0,
 expires_at TIMESTAMPTZ NOT NULL DEFAULT now()+interval '15 minutes'
);
