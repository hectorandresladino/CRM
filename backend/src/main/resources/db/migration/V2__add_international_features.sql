-- V2__add_international_features.sql
-- Adds tables for GDPR, multi-currency, workflow automation, lead scoring,
-- email templates, integrations, gamification, and client portal.

CREATE TABLE IF NOT EXISTS gdpr_consents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT,
    prospecto_id BIGINT,
    data_type VARCHAR(100) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    granted BOOLEAN NOT NULL,
    consent_text TEXT,
    version VARCHAR(20),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    withdrawn_at TIMESTAMP,
    granted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_gdpr_consents_tenant ON gdpr_consents(tenant_id);
CREATE INDEX IF NOT EXISTS idx_gdpr_consents_cliente ON gdpr_consents(cliente_id);

CREATE TABLE IF NOT EXISTS currency_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    base VARCHAR(3) NOT NULL,
    target VARCHAR(3) NOT NULL,
    rate DECIMAL(15, 6) NOT NULL,
    fetched_at TIMESTAMP,
    source VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, base, target)
);
CREATE INDEX IF NOT EXISTS idx_currency_rates_tenant ON currency_rates(tenant_id);

CREATE TABLE IF NOT EXISTS workflow_automations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    trigger_type VARCHAR(50) NOT NULL,
    trigger_config TEXT,
    action_type VARCHAR(50) NOT NULL,
    action_config TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    execution_count INT DEFAULT 0,
    last_executed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_workflow_tenant ON workflow_automations(tenant_id);
CREATE INDEX IF NOT EXISTS idx_workflow_trigger ON workflow_automations(tenant_id, trigger_type);

CREATE TABLE IF NOT EXISTS lead_scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    prospecto_id BIGINT NOT NULL,
    score INT NOT NULL,
    grade VARCHAR(2) NOT NULL,
    factors TEXT,
    email_engagement INT DEFAULT 0,
    website_visits INT DEFAULT 0,
    whatsapp_interactions INT DEFAULT 0,
    response_time_hours INT,
    company_size VARCHAR(50),
    budget_indicated BOOLEAN,
    decision_maker BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, prospecto_id)
);
CREATE INDEX IF NOT EXISTS idx_lead_scores_tenant ON lead_scores(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lead_scores_score ON lead_scores(tenant_id, score DESC);

CREATE TABLE IF NOT EXISTS email_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body_html TEXT NOT NULL,
    body_text TEXT,
    category VARCHAR(50) NOT NULL,
    thumbnail_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    usage_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_email_templates_tenant ON email_templates(tenant_id);
CREATE INDEX IF NOT EXISTS idx_email_templates_category ON email_templates(tenant_id, category);

CREATE TABLE IF NOT EXISTS integrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    connected BOOLEAN NOT NULL DEFAULT FALSE,
    credentials TEXT,
    sync_enabled BOOLEAN DEFAULT FALSE,
    last_sync_at TIMESTAMP,
    sync_frequency VARCHAR(20),
    config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, provider)
);
CREATE INDEX IF NOT EXISTS idx_integrations_tenant ON integrations(tenant_id);

CREATE TABLE IF NOT EXISTS gamification_badges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    criteria TEXT NOT NULL,
    points_required INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_gamification_tenant ON gamification_badges(tenant_id);

CREATE TABLE IF NOT EXISTS client_portal_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    portal_token VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_at TIMESTAMP,
    login_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_portal_tenant ON client_portal_access(tenant_id);
CREATE INDEX IF NOT EXISTS idx_portal_cliente ON client_portal_access(tenant_id, cliente_id);
