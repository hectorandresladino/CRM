-- V6: Add sentiment column to whatsapp_conversations and usage tracking tables

ALTER TABLE whatsapp_conversations ADD COLUMN IF NOT EXISTS sentiment VARCHAR(20);

-- Usage tracking per tenant
CREATE TABLE IF NOT EXISTS usage_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value BIGINT NOT NULL DEFAULT 0,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, metric_name, period_start)
);

CREATE INDEX IF NOT EXISTS idx_usage_tenant ON usage_tracking(tenant_id);
CREATE INDEX IF NOT EXISTS idx_usage_period ON usage_tracking(period_start);

-- Feature flags cache per tenant
CREATE TABLE IF NOT EXISTS feature_flags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    feature_key VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT false,
    config TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, feature_key)
);

CREATE INDEX IF NOT EXISTS idx_feature_flags_tenant ON feature_flags(tenant_id);
