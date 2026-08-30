-- V7: Salesforce-like features - Sales Cloud, Service Cloud, Revenue Cloud, Data 360, Platform, Flow, Agentforce, Analytics, Security

-- Sales Cloud
CREATE TABLE IF NOT EXISTS sales_forecasts (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    territory_id BIGINT,
    period_type VARCHAR(20) NOT NULL,
    period_year INT NOT NULL,
    period_quarter INT,
    period_month INT,
    forecast_amount DECIMAL(15,2),
    commit_amount DECIMAL(15,2),
    best_case_amount DECIMAL(15,2),
    closed_amount DECIMAL(15,2),
    pipeline_amount DECIMAL(15,2),
    forecast_category VARCHAR(20),
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_forecast_tenant ON sales_forecasts(tenant_id);
CREATE INDEX IF NOT EXISTS idx_forecast_period ON sales_forecasts(tenant_id, period_year);

CREATE TABLE IF NOT EXISTS territories (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    parent_id BIGINT,
    manager_id BIGINT,
    geo_region VARCHAR(100),
    countries VARCHAR(500),
    states VARCHAR(500),
    cities VARCHAR(500),
    zip_codes VARCHAR(500),
    target_revenue DECIMAL(15,2),
    account_count INT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_territory_tenant ON territories(tenant_id);

CREATE TABLE IF NOT EXISTS account_teams (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    team_role VARCHAR(50) NOT NULL,
    access_level VARCHAR(20) DEFAULT 'READ',
    is_primary BOOLEAN DEFAULT false,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_account_team ON account_teams(tenant_id, account_id);

CREATE TABLE IF NOT EXISTS opportunity_splits (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    split_percentage DECIMAL(5,2) NOT NULL,
    split_amount DECIMAL(15,2),
    split_type VARCHAR(20) DEFAULT 'REVENUE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_opp_split ON opportunity_splits(tenant_id, opportunity_id);

CREATE TABLE IF NOT EXISTS commissions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    sale_id BIGINT,
    commission_plan_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    commission_rate DECIMAL(5,2),
    sale_amount DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'CALCULATED',
    period_year INT,
    period_month INT,
    paid_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_commission_tenant ON commissions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_commission_period ON commissions(tenant_id, period_year, period_month);

CREATE TABLE IF NOT EXISTS sales_sequences (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    target_audience VARCHAR(500),
    step_count INT,
    duration_days INT,
    is_active BOOLEAN DEFAULT true,
    enrolled_count INT DEFAULT 0,
    reply_rate DOUBLE PRECISION,
    meeting_rate DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sequence_tenant ON sales_sequences(tenant_id);

-- Service Cloud
CREATE TABLE IF NOT EXISTS knowledge_articles (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(1000),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    tags VARCHAR(500),
    language VARCHAR(10) DEFAULT 'es',
    author_id BIGINT,
    view_count INT DEFAULT 0,
    helpful_count INT DEFAULT 0,
    not_helpful_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'DRAFT',
    published_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_knowledge_tenant ON knowledge_articles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_status ON knowledge_articles(tenant_id, status);

CREATE TABLE IF NOT EXISTS entitlements (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    contract_id BIGINT,
    name VARCHAR(200) NOT NULL,
    entitlement_type VARCHAR(50),
    sla_process_id BIGINT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    cases_remaining INT,
    total_cases INT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_entitlement_tenant ON entitlements(tenant_id);
CREATE INDEX IF NOT EXISTS idx_entitlement_client ON entitlements(tenant_id, client_id);

CREATE TABLE IF NOT EXISTS service_milestones (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    entitlement_id BIGINT NOT NULL,
    case_id BIGINT,
    name VARCHAR(200) NOT NULL,
    milestone_type VARCHAR(50),
    target_minutes INT,
    actual_minutes INT,
    is_violated BOOLEAN DEFAULT false,
    is_achieved BOOLEAN DEFAULT false,
    start_time TIMESTAMP,
    target_time TIMESTAMP,
    completion_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_milestone_tenant ON service_milestones(tenant_id);

CREATE TABLE IF NOT EXISTS field_service_orders (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    case_id BIGINT,
    title VARCHAR(500) NOT NULL,
    service_type VARCHAR(50),
    assigned_technician VARCHAR(200),
    scheduled_date TIMESTAMP,
    estimated_duration_min INT,
    actual_duration_min INT,
    service_address VARCHAR(500),
    service_lat DECIMAL(10,7),
    service_lng DECIMAL(10,7),
    contact_name VARCHAR(200),
    contact_phone VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'NORMAL',
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    work_notes TEXT,
    parts_used VARCHAR(500),
    cost_estimate DECIMAL(15,2),
    actual_cost DECIMAL(15,2),
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_field_service_tenant ON field_service_orders(tenant_id);
CREATE INDEX IF NOT EXISTS idx_field_service_status ON field_service_orders(tenant_id, status);

-- Revenue Cloud
CREATE TABLE IF NOT EXISTS subscription_amendments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    amendment_type VARCHAR(50) NOT NULL,
    effective_date TIMESTAMP,
    old_plan_id BIGINT,
    new_plan_id BIGINT,
    old_amount DECIMAL(15,2),
    new_amount DECIMAL(15,2),
    proration_amount DECIMAL(15,2),
    old_quantity INT,
    new_quantity INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    reason VARCHAR(500),
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_amendment_tenant ON subscription_amendments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_amendment_sub ON subscription_amendments(tenant_id, subscription_id);

CREATE TABLE IF NOT EXISTS usage_records (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,4) NOT NULL,
    unit VARCHAR(50),
    billing_period_start TIMESTAMP,
    billing_period_end TIMESTAMP,
    is_billed BOOLEAN DEFAULT false,
    invoice_id BIGINT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_usage_record_tenant ON usage_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_usage_record_sub ON usage_records(tenant_id, subscription_id);

CREATE TABLE IF NOT EXISTS dunning_campaigns (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    invoice_id BIGINT NOT NULL,
    client_id BIGINT NOT NULL,
    step_number INT,
    action_type VARCHAR(50),
    message_template TEXT,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    response_received BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_dunning_tenant ON dunning_campaigns(tenant_id);
CREATE INDEX IF NOT EXISTS idx_dunning_invoice ON dunning_campaigns(tenant_id, invoice_id);

-- Data 360
CREATE TABLE IF NOT EXISTS customer_events (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    client_id BIGINT,
    prospecto_id BIGINT,
    unified_profile_id VARCHAR(100),
    event_type VARCHAR(50) NOT NULL,
    event_source VARCHAR(50),
    event_channel VARCHAR(50),
    event_data TEXT,
    page_url VARCHAR(1000),
    referrer VARCHAR(1000),
    user_agent VARCHAR(500),
    ip_address VARCHAR(50),
    session_id VARCHAR(100),
    device_type VARCHAR(50),
    location_country VARCHAR(50),
    location_city VARCHAR(100),
    event_timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_event_tenant ON customer_events(tenant_id);
CREATE INDEX IF NOT EXISTS idx_event_client ON customer_events(tenant_id, client_id);
CREATE INDEX IF NOT EXISTS idx_event_type ON customer_events(tenant_id, event_type);

CREATE TABLE IF NOT EXISTS unified_profiles (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    profile_uuid VARCHAR(100) NOT NULL UNIQUE,
    client_id BIGINT,
    prospecto_id BIGINT,
    primary_email VARCHAR(200),
    primary_phone VARCHAR(50),
    full_name VARCHAR(200),
    company VARCHAR(200),
    identity_sources VARCHAR(500),
    match_confidence DOUBLE PRECISION,
    total_events INT DEFAULT 0,
    last_event_at TIMESTAMP,
    lifecycle_stage VARCHAR(50) DEFAULT 'UNKNOWN',
    segments VARCHAR(500),
    attributes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_profile_tenant ON unified_profiles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_profile_email ON unified_profiles(tenant_id, primary_email);
CREATE INDEX IF NOT EXISTS idx_profile_uuid ON unified_profiles(tenant_id, profile_uuid);

CREATE TABLE IF NOT EXISTS customer_segments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    segment_type VARCHAR(50),
    criteria TEXT,
    member_count INT DEFAULT 0,
    is_dynamic BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    last_evaluated_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_segment_tenant ON customer_segments(tenant_id);

-- Platform
CREATE TABLE IF NOT EXISTS custom_objects (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    api_name VARCHAR(200) NOT NULL,
    plural_label VARCHAR(200),
    icon_name VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    enable_activities BOOLEAN DEFAULT false,
    enable_history BOOLEAN DEFAULT true,
    enable_reports BOOLEAN DEFAULT true,
    enable_search BOOLEAN DEFAULT true,
    field_definitions TEXT,
    record_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_custom_obj_tenant ON custom_objects(tenant_id);

CREATE TABLE IF NOT EXISTS validation_rules (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    object_name VARCHAR(200) NOT NULL,
    field_name VARCHAR(200),
    formula TEXT NOT NULL,
    error_message VARCHAR(500) NOT NULL,
    error_location VARCHAR(20) DEFAULT 'FIELD',
    is_active BOOLEAN DEFAULT true,
    priority INT DEFAULT 1,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_validation_tenant ON validation_rules(tenant_id);
CREATE INDEX IF NOT EXISTS idx_validation_obj ON validation_rules(tenant_id, object_name);

CREATE TABLE IF NOT EXISTS approval_processes (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    object_name VARCHAR(200) NOT NULL,
    description TEXT,
    entry_criteria TEXT,
    approval_steps TEXT,
    is_active BOOLEAN DEFAULT true,
    allow_recall BOOLEAN DEFAULT true,
    approved_action VARCHAR(200),
    rejected_action VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_approval_tenant ON approval_processes(tenant_id);

-- Flow
CREATE TABLE IF NOT EXISTS flow_definitions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    flow_type VARCHAR(50) NOT NULL,
    trigger_type VARCHAR(50),
    trigger_object VARCHAR(200),
    trigger_condition TEXT,
    flow_steps TEXT,
    is_active BOOLEAN DEFAULT true,
    version INT DEFAULT 1,
    run_count INT DEFAULT 0,
    last_run_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_flow_tenant ON flow_definitions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_flow_trigger ON flow_definitions(tenant_id, trigger_object);

-- Agentforce
CREATE TABLE IF NOT EXISTS ai_predictions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    prediction_type VARCHAR(50) NOT NULL,
    target_entity VARCHAR(50),
    target_id BIGINT,
    predicted_value DECIMAL(15,2),
    probability DOUBLE PRECISION,
    confidence_score DOUBLE PRECISION,
    model_name VARCHAR(100),
    model_version VARCHAR(50),
    features_used TEXT,
    explanation TEXT,
    recommended_action VARCHAR(200),
    is_actioned BOOLEAN DEFAULT false,
    actioned_at TIMESTAMP,
    predicted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_prediction_tenant ON ai_predictions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_prediction_type ON ai_predictions(tenant_id, prediction_type);

-- Analytics
CREATE TABLE IF NOT EXISTS analytics_dashboards (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    dashboard_type VARCHAR(50),
    folder_id BIGINT,
    owner_id BIGINT,
    is_shared BOOLEAN DEFAULT false,
    shared_with VARCHAR(500),
    widgets TEXT,
    filters TEXT,
    refresh_frequency VARCHAR(20) DEFAULT 'MANUAL',
    last_refreshed_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_dashboard_tenant ON analytics_dashboards(tenant_id);

-- Experience Cloud
CREATE TABLE IF NOT EXISTS portal_configs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    portal_type VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    custom_domain VARCHAR(200),
    theme_primary_color VARCHAR(20),
    theme_secondary_color VARCHAR(20),
    logo_url VARCHAR(500),
    header_html TEXT,
    footer_html TEXT,
    visible_objects TEXT,
    self_service_actions TEXT,
    require_login BOOLEAN DEFAULT true,
    allow_registration BOOLEAN DEFAULT true,
    default_language VARCHAR(10) DEFAULT 'es',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_portal_tenant ON portal_configs(tenant_id);

-- Enterprise Security
CREATE TABLE IF NOT EXISTS permission_sets (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    object_permissions TEXT,
    field_permissions TEXT,
    tab_permissions TEXT,
    is_custom BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_perm_set_tenant ON permission_sets(tenant_id);

CREATE TABLE IF NOT EXISTS sharing_rules (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    object_name VARCHAR(200) NOT NULL,
    rule_type VARCHAR(50),
    share_with_type VARCHAR(50),
    share_with_id BIGINT,
    criteria_field VARCHAR(200),
    criteria_operator VARCHAR(50),
    criteria_value VARCHAR(500),
    access_level VARCHAR(20) DEFAULT 'READ',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sharing_tenant ON sharing_rules(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sharing_obj ON sharing_rules(tenant_id, object_name);
