-- V11: Service Cloud advanced - case comments, attachments, live chat

-- Case comments
CREATE TABLE IF NOT EXISTS case_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    case_id BIGINT NOT NULL,
    author_id BIGINT,
    author_name VARCHAR(100),
    body TEXT NOT NULL,
    is_public BOOLEAN DEFAULT TRUE,
    is_internal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_case_comments_tenant ON case_comments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_case_comments_case ON case_comments(case_id);

-- Case attachments
CREATE TABLE IF NOT EXISTS case_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    case_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT,
    file_url VARCHAR(500),
    uploaded_by BIGINT,
    uploaded_by_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_case_attachments_tenant ON case_attachments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_case_attachments_case ON case_attachments(case_id);

-- Live chat sessions
CREATE TABLE IF NOT EXISTS live_chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    client_id BIGINT,
    contact_id BIGINT,
    visitor_name VARCHAR(100),
    visitor_email VARCHAR(255),
    visitor_ip VARCHAR(45),
    visitor_country VARCHAR(100),
    visitor_city VARCHAR(100),
    page_url VARCHAR(500),
    assigned_agent_id BIGINT,
    assigned_agent_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    picked_up_at TIMESTAMP,
    ended_at TIMESTAMP,
    wait_time_seconds INT,
    duration_seconds INT,
    satisfaction_score INT,
    transcript TEXT
);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_tenant ON live_chat_sessions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_status ON live_chat_sessions(status);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_agent ON live_chat_sessions(assigned_agent_id);

-- Chat messages
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    sender_type VARCHAR(20) NOT NULL,
    sender_name VARCHAR(100),
    content TEXT NOT NULL,
    is_system_message BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_chat_messages_tenant ON chat_messages(tenant_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session ON chat_messages(session_id);
