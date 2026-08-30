-- Keep upgraded production databases aligned with the commercial plan matrix.
-- V9 introduced unlimited seats but older plan rows retained storage/client caps.

-- V6 created feature_flags with legacy column names. Because V15 used
-- CREATE TABLE IF NOT EXISTS, its expanded shape was never applied on upgrades.
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS key_name VARCHAR(100);
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS is_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS rollout_percentage INT DEFAULT 0;
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS target_segments TEXT;
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS metadata TEXT;
ALTER TABLE feature_flags ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE feature_flags
SET key_name = COALESCE(key_name, feature_key),
    is_enabled = enabled,
    metadata = COALESCE(metadata, config);
ALTER TABLE feature_flags ALTER COLUMN key_name SET NOT NULL;
ALTER TABLE feature_flags ALTER COLUMN feature_key DROP NOT NULL;

CREATE TABLE IF NOT EXISTS currencies (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    code VARCHAR(3) NOT NULL,
    name VARCHAR(255) NOT NULL,
    symbol VARCHAR(5),
    is_base BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    decimal_places INT DEFAULT 2,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, code)
);
CREATE INDEX IF NOT EXISTS idx_currencies_tenant ON currencies(tenant_id);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    notification_type VARCHAR(50),
    entity_type VARCHAR(100),
    entity_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_notifications_tenant ON notifications(tenant_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(tenant_id, user_id, is_read);

INSERT INTO plans (name, description, price_monthly, price_yearly, currency, max_users,
    max_contacts, max_clients, max_storage_mb, max_automations, max_whatsapp_messages,
    max_emails, max_sms, max_api_calls, max_ai_predictions, max_sub_accounts,
    is_agency_plan, has_whatsapp, has_email_marketing, has_api_access, has_white_label,
    has_ai_features, has_advanced_reports, has_webhooks, trial_days, active, created_at)
SELECT 'STARTER', 'Plan inicial para pequeñas empresas - usuarios ilimitados', 29, 290, 'USD', NULL,
    500, 500, 2048, 10, 100, 1000, 50, 1000, 50, 0,
    FALSE, FALSE, TRUE, FALSE, FALSE, FALSE, FALSE, FALSE, 14, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'STARTER');

INSERT INTO plans (name, description, price_monthly, price_yearly, currency, max_users,
    max_contacts, max_clients, max_storage_mb, max_automations, max_whatsapp_messages,
    max_emails, max_sms, max_api_calls, max_ai_predictions, max_sub_accounts,
    is_agency_plan, has_whatsapp, has_email_marketing, has_api_access, has_white_label,
    has_ai_features, has_advanced_reports, has_webhooks, trial_days, active, created_at)
SELECT 'BUSINESS', 'Plan para empresas en crecimiento - usuarios ilimitados', 79, 790, 'USD', NULL,
    5000, 5000, 20480, 100, 1000, 10000, 500, 10000, 500, 0,
    FALSE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, TRUE, 14, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'BUSINESS');

INSERT INTO plans (name, description, price_monthly, price_yearly, currency, max_users,
    max_contacts, max_clients, max_storage_mb, max_automations, max_whatsapp_messages,
    max_emails, max_sms, max_api_calls, max_ai_predictions, max_sub_accounts,
    is_agency_plan, has_whatsapp, has_email_marketing, has_api_access, has_white_label,
    has_ai_features, has_advanced_reports, has_webhooks, trial_days, active, created_at)
SELECT 'ENTERPRISE', 'Plan empresarial - usuarios ilimitados', 199, 1990, 'USD', NULL,
    50000, 50000, 204800, 999, 10000, 100000, 5000, 100000, 5000, 0,
    FALSE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 30, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'ENTERPRISE');

UPDATE plans
SET max_users = NULL,
    max_contacts = 500,
    max_clients = 500,
    max_storage_mb = 2048
WHERE name = 'STARTER';

UPDATE plans
SET max_users = NULL,
    max_contacts = 5000,
    max_clients = 5000,
    max_storage_mb = 20480
WHERE name = 'BUSINESS';

UPDATE plans
SET max_users = NULL,
    max_contacts = 50000,
    max_clients = 50000,
    max_storage_mb = 204800
WHERE name = 'ENTERPRISE';

UPDATE plans
SET max_users = NULL,
    max_contacts = 100000,
    max_clients = 100000,
    max_storage_mb = 512000,
    max_sub_accounts = 50
WHERE name = 'AGENCY';
