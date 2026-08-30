-- V15: Platform & Infrastructure - feature flags, settings, rate limits, file storage, backups

-- Feature flags
CREATE TABLE IF NOT EXISTS feature_flags (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_enabled BOOLEAN DEFAULT FALSE,
    rollout_percentage INT DEFAULT 0,
    target_segments TEXT,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_feature_flags_tenant ON feature_flags(tenant_id);

-- System settings
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    "value" TEXT NOT NULL,
    description TEXT,
    data_type VARCHAR(20),
    is_public BOOLEAN DEFAULT FALSE,
    category VARCHAR(50),
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_settings_tenant ON system_settings(tenant_id);

-- Rate limit configs
CREATE TABLE IF NOT EXISTS rate_limit_configs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    requests_per_minute INT DEFAULT 100,
    requests_per_hour INT DEFAULT 1000,
    requests_per_day INT DEFAULT 10000,
    burst_limit INT DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    current_requests_minute INT DEFAULT 0,
    current_requests_hour INT DEFAULT 0,
    current_requests_day INT DEFAULT 0,
    last_reset_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rate_limits_tenant ON rate_limit_configs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_rate_limits_endpoint ON rate_limit_configs(endpoint);

-- File storage records
CREATE TABLE IF NOT EXISTS file_storage_records (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    file_url VARCHAR(500),
    file_type VARCHAR(100),
    file_size_bytes BIGINT,
    storage_provider VARCHAR(50),
    bucket_name VARCHAR(100),
    entity_type VARCHAR(50),
    entity_id BIGINT,
    uploaded_by BIGINT,
    is_public BOOLEAN DEFAULT FALSE,
    download_count INT DEFAULT 0,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_file_storage_tenant ON file_storage_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_file_storage_entity ON file_storage_records(entity_type, entity_id);

-- Backup records
CREATE TABLE IF NOT EXISTS backup_records (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    file_url VARCHAR(500),
    file_size_bytes BIGINT,
    record_count INT,
    table_count INT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP,
    initiated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_backups_tenant ON backup_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_backups_status ON backup_records(status);
