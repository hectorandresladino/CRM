-- V18: replace global business-key uniqueness with tenant-scoped uniqueness.
-- Production uses PostgreSQL; IF EXISTS keeps this safe for databases created
-- before constraint names were made explicit.

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_username_key;
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_email_key;
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS clientes_email_key;
ALTER TABLE prospectos DROP CONSTRAINT IF EXISTS prospectos_email_key;

CREATE UNIQUE INDEX IF NOT EXISTS uk_usuarios_tenant_username
    ON usuarios (tenant_id, username);
CREATE UNIQUE INDEX IF NOT EXISTS uk_usuarios_tenant_email
    ON usuarios (tenant_id, email);
CREATE UNIQUE INDEX IF NOT EXISTS uk_clientes_tenant_email
    ON clientes (tenant_id, email);
CREATE UNIQUE INDEX IF NOT EXISTS uk_clientes_tenant_identificacion
    ON clientes (tenant_id, identificacion);
CREATE UNIQUE INDEX IF NOT EXISTS uk_prospectos_tenant_email
    ON prospectos (tenant_id, email);
