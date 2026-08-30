-- V8: Multi-tenant constraints, MFA recovery codes, RBAC tables

-- Add MFA recovery codes column
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS mfa_recovery_codes TEXT;

-- Change unique constraints from global to per-tenant
-- Drop global unique constraints
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS uk_usuarios_username;
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS uk_usuarios_email;

-- Tenant-scoped uniqueness is created in V18 after legacy global constraints
-- are removed. PostgreSQL does not support ADD CONSTRAINT IF NOT EXISTS.

-- RBAC: Role permissions audit
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT,
    role VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    operations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, role, module)
);
CREATE INDEX IF NOT EXISTS idx_role_perms_tenant ON role_permissions(tenant_id);

-- MFA audit log
CREATE TABLE IF NOT EXISTS mfa_audit_log (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45),
    success BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_mfa_audit_tenant ON mfa_audit_log(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mfa_audit_user ON mfa_audit_log(user_id);

-- Tenant isolation test results
CREATE TABLE IF NOT EXISTS tenant_isolation_test (
    id BIGSERIAL PRIMARY KEY,
    test_name VARCHAR(200) NOT NULL,
    tenant_a BIGINT,
    tenant_b BIGINT,
    entity_type VARCHAR(100),
    passed BOOLEAN,
    error_message TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
