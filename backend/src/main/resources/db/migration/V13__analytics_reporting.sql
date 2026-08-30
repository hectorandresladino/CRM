-- V13: Analytics & Reporting advanced - reports, KPIs, data exports

-- Report definitions
CREATE TABLE IF NOT EXISTS report_definitions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    data_source VARCHAR(100),
    query_config TEXT,
    chart_type VARCHAR(50),
    group_by VARCHAR(100),
    date_range_field VARCHAR(100),
    default_date_range VARCHAR(50),
    columns TEXT,
    filters TEXT,
    sort_config TEXT,
    is_scheduled BOOLEAN DEFAULT FALSE,
    schedule_cron VARCHAR(100),
    schedule_emails TEXT,
    format VARCHAR(10) DEFAULT 'PDF',
    owner_id BIGINT,
    is_shared BOOLEAN DEFAULT FALSE,
    folder_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_reports_tenant ON report_definitions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_reports_owner ON report_definitions(owner_id);

-- Report executions
CREATE TABLE IF NOT EXISTS report_executions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    report_id BIGINT NOT NULL,
    report_name VARCHAR(255),
    executed_by BIGINT,
    executed_by_name VARCHAR(100),
    row_count INT,
    execution_time_ms BIGINT,
    file_url VARCHAR(500),
    file_size_bytes BIGINT,
    format VARCHAR(10),
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    error_message TEXT,
    parameters TEXT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_executions_tenant ON report_executions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_executions_report ON report_executions(report_id);

-- KPI definitions
CREATE TABLE IF NOT EXISTS kpi_definitions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    metric_name VARCHAR(100),
    aggregation_type VARCHAR(20),
    data_source VARCHAR(100),
    target_value DECIMAL(15,2),
    warning_threshold DECIMAL(15,2),
    unit VARCHAR(20),
    period_type VARCHAR(20),
    owner_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_kpis_tenant ON kpi_definitions(tenant_id);

-- KPI snapshots
CREATE TABLE IF NOT EXISTS kpi_snapshots (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    kpi_id BIGINT NOT NULL,
    actual_value DECIMAL(15,2),
    target_value DECIMAL(15,2),
    attainment_percentage DOUBLE PRECISION,
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    trend VARCHAR(10),
    previous_value DECIMAL(15,2),
    change_percentage DOUBLE PRECISION,
    status VARCHAR(20),
    snapshot_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_kpi_snapshots_tenant ON kpi_snapshots(tenant_id);
CREATE INDEX IF NOT EXISTS idx_kpi_snapshots_kpi ON kpi_snapshots(kpi_id);

-- Data exports
CREATE TABLE IF NOT EXISTS data_exports (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50),
    export_format VARCHAR(10),
    filter_criteria TEXT,
    total_records INT,
    file_url VARCHAR(500),
    file_size_bytes BIGINT,
    requested_by BIGINT,
    requested_by_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_exports_tenant ON data_exports(tenant_id);
CREATE INDEX IF NOT EXISTS idx_exports_status ON data_exports(status);
