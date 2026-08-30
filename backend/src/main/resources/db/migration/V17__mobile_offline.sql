-- V17: Mobile & Offline advanced - devices, sync queue, push notifications, app config, conflicts, usage stats

-- Mobile devices
CREATE TABLE IF NOT EXISTS mobile_devices (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    device_name VARCHAR(255),
    device_model VARCHAR(255),
    os_type VARCHAR(10),
    os_version VARCHAR(50),
    app_version VARCHAR(50),
    push_token VARCHAR(500),
    device_uuid VARCHAR(255),
    is_registered BOOLEAN DEFAULT TRUE,
    last_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_mobile_devices_tenant ON mobile_devices(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mobile_devices_user ON mobile_devices(user_id);

-- Offline sync queue
CREATE TABLE IF NOT EXISTS offline_sync_queues (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    device_id BIGINT,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    operation VARCHAR(10),
    payload TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempts INT DEFAULT 0,
    max_attempts INT DEFAULT 5,
    error_message TEXT,
    queued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    synced_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sync_queue_tenant ON offline_sync_queues(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sync_queue_status ON offline_sync_queues(status);

-- Push notification logs
CREATE TABLE IF NOT EXISTS push_notification_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    device_id BIGINT,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    notification_type VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id BIGINT,
    data_payload TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    provider_message_id VARCHAR(255),
    error_message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_push_logs_tenant ON push_notification_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_push_logs_user ON push_notification_logs(user_id);

-- Mobile app configs
CREATE TABLE IF NOT EXISTS mobile_app_configs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    config_key VARCHAR(100),
    config_value TEXT,
    min_app_version VARCHAR(50),
    force_update BOOLEAN DEFAULT FALSE,
    maintenance_mode BOOLEAN DEFAULT FALSE,
    maintenance_message TEXT,
    offline_cache_days INT DEFAULT 7,
    max_offline_records INT DEFAULT 1000,
    sync_interval_seconds INT DEFAULT 60,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_app_config_tenant ON mobile_app_configs(tenant_id);

-- Sync conflicts
CREATE TABLE IF NOT EXISTS sync_conflicts (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    device_id BIGINT,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    server_version TEXT,
    client_version TEXT,
    server_updated_at TIMESTAMP,
    client_updated_at TIMESTAMP,
    resolution VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    resolved_data TEXT,
    resolved_by BIGINT,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sync_conflicts_tenant ON sync_conflicts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sync_conflicts_resolution ON sync_conflicts(resolution);

-- Mobile usage stats
CREATE TABLE IF NOT EXISTS mobile_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    device_id BIGINT,
    stat_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_count INT DEFAULT 0,
    total_session_minutes INT DEFAULT 0,
    features_used TEXT,
    offline_actions INT DEFAULT 0,
    sync_count INT DEFAULT 0,
    data_synced_bytes BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_mobile_usage_tenant ON mobile_usage_stats(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mobile_usage_user ON mobile_usage_stats(user_id);
