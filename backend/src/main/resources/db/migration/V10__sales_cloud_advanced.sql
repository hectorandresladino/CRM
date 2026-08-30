-- V10: Sales Cloud advanced - accounts, contacts, competitors, calendar, booking, email sync

-- Accounts (B2B companies with hierarchy)
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    parent_account_id BIGINT,
    industry VARCHAR(100),
    website VARCHAR(255),
    phone VARCHAR(50),
    billing_street VARCHAR(255),
    billing_city VARCHAR(100),
    billing_state VARCHAR(100),
    billing_country VARCHAR(100),
    billing_postal_code VARCHAR(20),
    shipping_street VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_country VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    annual_revenue DECIMAL(15,2),
    employee_count INT,
    account_type VARCHAR(50),
    territory_id BIGINT,
    owner_id BIGINT,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_accounts_tenant ON accounts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_accounts_parent ON accounts(parent_account_id);
CREATE INDEX IF NOT EXISTS idx_accounts_owner ON accounts(owner_id);

-- Contacts (individual people linked to accounts)
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    account_id BIGINT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    mobile VARCHAR(50),
    title VARCHAR(100),
    department VARCHAR(100),
    owner_id BIGINT,
    reporting_to BIGINT,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    lead_source VARCHAR(100),
    do_not_call BOOLEAN DEFAULT FALSE,
    email_opt_out BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_contacts_tenant ON contacts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_contacts_account ON contacts(account_id);
CREATE INDEX IF NOT EXISTS idx_contacts_owner ON contacts(owner_id);

-- Opportunity competitors
CREATE TABLE IF NOT EXISTS opportunity_competitors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    competitor_name VARCHAR(255) NOT NULL,
    strengths TEXT,
    weaknesses TEXT,
    threat_level VARCHAR(20),
    our_position VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_competitors_tenant ON opportunity_competitors(tenant_id);
CREATE INDEX IF NOT EXISTS idx_competitors_opp ON opportunity_competitors(opportunity_id);

-- Calendar events
CREATE TABLE IF NOT EXISTS calendar_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    owner_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    all_day BOOLEAN DEFAULT FALSE,
    location VARCHAR(255),
    meeting_type VARCHAR(50),
    account_id BIGINT,
    contact_id BIGINT,
    opportunity_id BIGINT,
    attendee_emails TEXT,
    google_event_id VARCHAR(255),
    outlook_event_id VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_calendar_tenant ON calendar_events(tenant_id);
CREATE INDEX IF NOT EXISTS idx_calendar_owner ON calendar_events(owner_id);
CREATE INDEX IF NOT EXISTS idx_calendar_start ON calendar_events(start_time);

-- Booking pages
CREATE TABLE IF NOT EXISTS booking_pages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT DEFAULT 30,
    buffer_minutes INT DEFAULT 15,
    min_notice_hours INT DEFAULT 24,
    max_advance_days INT DEFAULT 30,
    available_days TEXT,
    start_hour INT DEFAULT 9,
    end_hour INT DEFAULT 18,
    timezone VARCHAR(50),
    meeting_link_provider VARCHAR(50),
    confirmation_mode VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_booking_tenant ON booking_pages(tenant_id);
CREATE INDEX IF NOT EXISTS idx_booking_owner ON booking_pages(owner_id);

-- Email sync log
CREATE TABLE IF NOT EXISTS email_sync_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    message_id VARCHAR(255),
    thread_id VARCHAR(255),
    subject VARCHAR(500) NOT NULL,
    sender_email VARCHAR(255),
    recipient_emails TEXT,
    body TEXT,
    is_incoming BOOLEAN,
    is_read BOOLEAN DEFAULT FALSE,
    is_replied BOOLEAN DEFAULT FALSE,
    opened_at TIMESTAMP,
    clicked_at TIMESTAMP,
    replied_at TIMESTAMP,
    account_id BIGINT,
    contact_id BIGINT,
    opportunity_id BIGINT,
    synced_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_email_tenant ON email_sync_log(tenant_id);
CREATE INDEX IF NOT EXISTS idx_email_user ON email_sync_log(user_id);
CREATE INDEX IF NOT EXISTS idx_email_contact ON email_sync_log(contact_id);
