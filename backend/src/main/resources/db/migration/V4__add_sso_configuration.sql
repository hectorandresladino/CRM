-- V4__add_sso_configuration.sql

CREATE TABLE IF NOT EXISTS sso_configurations (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    protocol VARCHAR(10) NOT NULL,
    client_id VARCHAR(500),
    client_secret VARCHAR(500),
    tenant_uuid VARCHAR(200),
    redirect_uri VARCHAR(500),
    metadata_url VARCHAR(500),
    idp_entity_id VARCHAR(300),
    idp_sso_url VARCHAR(500),
    idp_certificate TEXT,
    sp_entity_id VARCHAR(300),
    attribute_mapping TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    auto_provision BOOLEAN DEFAULT TRUE,
    default_role VARCHAR(50) DEFAULT 'VENDEDOR',
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sso_tenant ON sso_configurations(tenant_id);
