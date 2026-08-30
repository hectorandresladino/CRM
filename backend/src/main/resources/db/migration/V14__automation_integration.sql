-- V14: Automation & Integration advanced - execution logs, webhook logs, sync logs, scheduled jobs

-- Flow execution logs
CREATE TABLE IF NOT EXISTS flow_execution_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    flow_id BIGINT NOT NULL,
    flow_name VARCHAR(255),
    trigger_type VARCHAR(50),
    trigger_entity VARCHAR(100),
    trigger_entity_id BIGINT,
    trigger_data TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    steps_completed INT DEFAULT 0,
    steps_total INT DEFAULT 0,
    error_message TEXT,
    error_step VARCHAR(255),
    duration_ms BIGINT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_flow_logs_tenant ON flow_execution_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_flow_logs_flow ON flow_execution_logs(flow_id);
CREATE INDEX IF NOT EXISTS idx_flow_logs_status ON flow_execution_logs(status);

-- Webhook delivery logs
CREATE TABLE IF NOT EXISTS webhook_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    webhook_id BIGINT NOT NULL,
    event_type VARCHAR(100),
    payload TEXT,
    response_status INT,
    response_body TEXT,
    response_time_ms BIGINT,
    attempt_number INT DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_webhook_logs_tenant ON webhook_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_webhook_logs_webhook ON webhook_logs(webhook_id);
CREATE INDEX IF NOT EXISTS idx_webhook_logs_status ON webhook_logs(status);

-- Integration sync logs
CREATE TABLE IF NOT EXISTS integration_sync_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    integration_id BIGINT NOT NULL,
    provider VARCHAR(50),
    sync_direction VARCHAR(20),
    records_processed INT DEFAULT 0,
    records_created INT DEFAULT 0,
    records_updated INT DEFAULT 0,
    records_failed INT DEFAULT 0,
    error_details TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'RUNNING',
    duration_ms BIGINT,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sync_logs_tenant ON integration_sync_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sync_logs_integration ON integration_sync_logs(integration_id);

-- Scheduled jobs
CREATE TABLE IF NOT EXISTS scheduled_jobs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    job_type VARCHAR(50),
    cron_expression VARCHAR(100),
    target_entity VARCHAR(100),
    action_config TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP,
    run_count INT DEFAULT 0,
    failure_count INT DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_scheduled_jobs_tenant ON scheduled_jobs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_scheduled_jobs_active ON scheduled_jobs(is_active);
