-- V3__add_whatsapp_ai_cpq_esignature.sql

CREATE TABLE IF NOT EXISTS whatsapp_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    contact_name VARCHAR(200),
    prospecto_id BIGINT,
    cliente_id BIGINT,
    direction VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    message_type VARCHAR(20),
    ai_response BOOLEAN DEFAULT FALSE,
    ai_intent VARCHAR(50),
    ai_confidence DOUBLE,
    ai_handled BOOLEAN DEFAULT TRUE,
    human_taken_over BOOLEAN DEFAULT FALSE,
    assigned_agent VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_wa_conv_tenant ON whatsapp_conversations(tenant_id);
CREATE INDEX IF NOT EXISTS idx_wa_conv_phone ON whatsapp_conversations(tenant_id, contact_phone);

CREATE TABLE IF NOT EXISTS whatsapp_ai_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN DEFAULT TRUE,
    auto_reply BOOLEAN DEFAULT TRUE,
    business_name VARCHAR(200),
    welcome_message TEXT,
    fallback_message TEXT,
    hours_start VARCHAR(5) DEFAULT '08:00',
    hours_end VARCHAR(5) DEFAULT '18:00',
    out_of_hours_message TEXT,
    qualify_leads BOOLEAN DEFAULT TRUE,
    transcribe_audio BOOLEAN DEFAULT TRUE,
    language VARCHAR(5) DEFAULT 'es',
    personality VARCHAR(20) DEFAULT 'professional',
    system_prompt TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cpq_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    base_price DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    min_discount_pct DECIMAL(5, 2),
    max_discount_pct DECIMAL(5, 2),
    cost_price DECIMAL(15, 2),
    min_margin_pct DECIMAL(5, 2),
    is_active BOOLEAN DEFAULT TRUE,
    category VARCHAR(100),
    unit VARCHAR(20) DEFAULT 'unit',
    stock INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, sku)
);
CREATE INDEX IF NOT EXISTS idx_cpq_prod_tenant ON cpq_products(tenant_id);

CREATE TABLE IF NOT EXISTS cpq_quote_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cotizacion_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name VARCHAR(200) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    discount_pct DECIMAL(5, 2),
    line_total DECIMAL(15, 2) NOT NULL,
    approval_required BOOLEAN DEFAULT FALSE,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_cpq_qi_tenant ON cpq_quote_items(tenant_id);
CREATE INDEX IF NOT EXISTS idx_cpq_qi_cotiz ON cpq_quote_items(tenant_id, cotizacion_id);

CREATE TABLE IF NOT EXISTS esignature_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    contrato_id BIGINT,
    document_title VARCHAR(300) NOT NULL,
    document_url VARCHAR(500),
    signer_name VARCHAR(200) NOT NULL,
    signer_email VARCHAR(255) NOT NULL,
    signer_phone VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    signature_token VARCHAR(255) NOT NULL UNIQUE,
    signed_at TIMESTAMP,
    signature_hash VARCHAR(255),
    signer_ip VARCHAR(45),
    expires_at TIMESTAMP,
    audit_trail TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_esign_tenant ON esignature_requests(tenant_id);
