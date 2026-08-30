-- V9: New plans schema, agency mode, payment provider fields

-- Add new limit columns to plans
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_contacts INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_whatsapp_messages INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_emails INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_sms INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_api_calls INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_ai_predictions INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS max_sub_accounts INT;
ALTER TABLE plans ADD COLUMN IF NOT EXISTS is_agency_plan BOOLEAN DEFAULT FALSE;

-- Add agency mode columns to tenants
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS parent_tenant_id BIGINT;
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS is_agency BOOLEAN DEFAULT FALSE;
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS is_sub_account BOOLEAN DEFAULT FALSE;

-- Add transaction_id to billing_invoices
ALTER TABLE billing_invoices ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(255);

-- Add gateway_token to payment_methods
ALTER TABLE payment_methods ADD COLUMN IF NOT EXISTS gateway_token VARCHAR(500);

-- Add mfa_recovery_codes to usuarios (if not already from V8)
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS mfa_recovery_codes TEXT;

-- Set max_users to NULL for all plans (unlimited users)
UPDATE plans SET max_users = NULL;

-- Insert AGENCY plan if not exists
INSERT INTO plans (name, description, price_monthly, price_yearly, currency, max_users, max_contacts, max_clients, max_storage_mb, max_automations, max_whatsapp_messages, max_emails, max_sms, max_api_calls, max_ai_predictions, max_sub_accounts, is_agency_plan, has_whatsapp, has_email_marketing, has_api_access, has_white_label, has_ai_features, has_advanced_reports, has_webhooks, trial_days, active, created_at)
SELECT 'AGENCY', 'Plan para agencias con subcuentas y white-label', 399, 3990, 'USD', NULL, 100000, 100000, 512000, 999, 50000, 500000, 25000, 500000, 25000, 50, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, 30, TRUE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM plans WHERE name = 'AGENCY');

-- Rename PROFESSIONAL to BUSINESS
UPDATE plans SET name = 'BUSINESS', description = 'Plan business para empresas en crecimiento - usuarios ilimitados' WHERE name = 'PROFESSIONAL';

-- Update STARTER limits
UPDATE plans SET max_contacts = 500, max_whatsapp_messages = 100, max_emails = 1000, max_sms = 50, max_api_calls = 1000, max_ai_predictions = 50, max_sub_accounts = 0 WHERE name = 'STARTER';

-- Update BUSINESS limits
UPDATE plans SET max_contacts = 5000, max_whatsapp_messages = 1000, max_emails = 10000, max_sms = 500, max_api_calls = 10000, max_ai_predictions = 500, max_sub_accounts = 0 WHERE name = 'BUSINESS';

-- Update ENTERPRISE limits
UPDATE plans SET max_contacts = 50000, max_whatsapp_messages = 10000, max_emails = 100000, max_sms = 5000, max_api_calls = 100000, max_ai_predictions = 5000, max_sub_accounts = 0 WHERE name = 'ENTERPRISE';

-- Index for agency sub-accounts
CREATE INDEX IF NOT EXISTS idx_tenants_parent ON tenants(parent_tenant_id);
