-- V16: Security & Compliance advanced - SSO, password policies, retention, GDPR, audits, IP whitelist, scans, sessions

-- SSO configs
CREATE TABLE IF NOT EXISTS sso_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    client_id VARCHAR(255),
    client_secret VARCHAR(500),
    tenant_external_id VARCHAR(255),
    metadata_url VARCHAR(500),
    certificate TEXT,
    is_enabled BOOLEAN DEFAULT FALSE,
    auto_provision_users BOOLEAN DEFAULT TRUE,
    default_role_id BIGINT,
    last_tested_at TIMESTAMP,
    last_test_result VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sso_tenant ON sso_configs(tenant_id);

-- Password policies
CREATE TABLE IF NOT EXISTS password_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    min_length INT DEFAULT 8,
    require_uppercase BOOLEAN DEFAULT TRUE,
    require_lowercase BOOLEAN DEFAULT TRUE,
    require_numbers BOOLEAN DEFAULT TRUE,
    require_special_chars BOOLEAN DEFAULT TRUE,
    special_chars VARCHAR(100),
    password_expiry_days INT DEFAULT 90,
    password_history_count INT DEFAULT 5,
    max_login_attempts INT DEFAULT 5,
    lockout_duration_minutes INT DEFAULT 30,
    session_timeout_minutes INT DEFAULT 60,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_password_policy_tenant ON password_policies(tenant_id);

-- Data retention policies
CREATE TABLE IF NOT EXISTS data_retention_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    entity_type VARCHAR(100),
    retention_days INT,
    action_type VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    last_executed_at TIMESTAMP,
    records_processed INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_retention_tenant ON data_retention_policies(tenant_id);

-- GDPR requests
CREATE TABLE IF NOT EXISTS gdpr_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    client_id BIGINT,
    client_email VARCHAR(255),
    client_name VARCHAR(255),
    request_type VARCHAR(20) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_to BIGINT,
    response_data TEXT,
    file_url VARCHAR(500),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_gdpr_requests_tenant ON gdpr_requests(tenant_id);
CREATE INDEX IF NOT EXISTS idx_gdpr_requests_status ON gdpr_requests(status);

-- Compliance audits
CREATE TABLE IF NOT EXISTS compliance_audits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    standard VARCHAR(20) NOT NULL,
    audit_type VARCHAR(50),
    auditor VARCHAR(255),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    result VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    findings TEXT,
    recommendations TEXT,
    score DOUBLE,
    report_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_compliance_tenant ON compliance_audits(tenant_id);

-- IP whitelist
CREATE TABLE IF NOT EXISTS ip_whitelists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    cidr_mask VARCHAR(10),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ip_whitelist_tenant ON ip_whitelists(tenant_id);

-- Security scans
CREATE TABLE IF NOT EXISTS security_scans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    vulnerabilities_found INT DEFAULT 0,
    critical_count INT DEFAULT 0,
    high_count INT DEFAULT 0,
    medium_count INT DEFAULT 0,
    low_count INT DEFAULT 0,
    scan_results TEXT,
    report_url VARCHAR(500),
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_security_scans_tenant ON security_scans(tenant_id);

-- Session records
CREATE TABLE IF NOT EXISTS session_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    user_email VARCHAR(255),
    session_token VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_type VARCHAR(50),
    location VARCHAR(255),
    login_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_at TIMESTAMP,
    last_activity_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_mfa_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sessions_tenant ON session_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sessions_user ON session_records(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_active ON session_records(is_active);
