-- ============================================
-- CRM SaaS - Esquema Multi-tenant Completo
-- ============================================

-- Tabla: tenants (Empresas)
CREATE TABLE IF NOT EXISTS tenants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    timezone VARCHAR(100) NOT NULL,
    locale VARCHAR(10) NOT NULL,
    logo_url VARCHAR(500),
    primary_color VARCHAR(20),
    custom_domain VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'TRIAL',
    plan_id BIGINT,
    trial_ends_at TIMESTAMP,
    suspended_at TIMESTAMP,
    suspended_reason TEXT,
    max_users INTEGER DEFAULT 5,
    max_clients INTEGER DEFAULT 100,
    max_storage_mb BIGINT DEFAULT 1024,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tabla: plans (Planes SaaS)
CREATE TABLE IF NOT EXISTS plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price_monthly DECIMAL(10,2) NOT NULL,
    price_yearly DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    max_users INTEGER,
    max_clients INTEGER,
    max_storage_mb BIGINT,
    max_automations INTEGER,
    has_whatsapp BOOLEAN DEFAULT FALSE,
    has_email_marketing BOOLEAN DEFAULT FALSE,
    has_api_access BOOLEAN DEFAULT FALSE,
    has_white_label BOOLEAN DEFAULT FALSE,
    has_ai_features BOOLEAN DEFAULT FALSE,
    has_advanced_reports BOOLEAN DEFAULT FALSE,
    has_webhooks BOOLEAN DEFAULT FALSE,
    trial_days INTEGER DEFAULT 14,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

-- Tabla: subscriptions (Suscripciones)
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'TRIAL',
    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY',
    current_period_start TIMESTAMP,
    current_period_end TIMESTAMP,
    trial_start TIMESTAMP,
    trial_end TIMESTAMP,
    auto_renew BOOLEAN DEFAULT TRUE,
    amount DECIMAL(10,2),
    currency VARCHAR(10),
    payment_method_id BIGINT,
    cancelled_at TIMESTAMP,
    cancel_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tabla: usuarios (con tenant_id y seguridad completa)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    rol VARCHAR(30) NOT NULL DEFAULT 'SALES',
    activo BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    email_verification_token VARCHAR(255),
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    last_login_at TIMESTAMP,
    last_login_ip VARCHAR(50),
    refresh_token VARCHAR(500),
    refresh_token_expires TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tabla: billing_invoices (Facturas SaaS)
CREATE TABLE IF NOT EXISTS billing_invoices (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    subscription_id BIGINT,
    number VARCHAR(100) NOT NULL UNIQUE,
    plan_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    issue_date TIMESTAMP,
    due_date TIMESTAMP,
    paid_date TIMESTAMP,
    billing_period_start TIMESTAMP,
    billing_period_end TIMESTAMP,
    pdf_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL
);

-- Tabla: payments (Pagos SaaS)
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    billing_invoice_id BIGINT,
    payment_method_id BIGINT,
    gateway VARCHAR(50) NOT NULL,
    gateway_transaction_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_date TIMESTAMP,
    gateway_response TEXT,
    created_at TIMESTAMP NOT NULL
);

-- Tabla: payment_methods (Metodos de pago)
CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    gateway_customer_id VARCHAR(255),
    last_four VARCHAR(4),
    card_brand VARCHAR(50),
    expiry_month INTEGER,
    expiry_year INTEGER,
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL
);

-- Tabla: audit_logs (Auditoria)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT,
    user_id BIGINT,
    user_name VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    entity VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL
);

-- ============================================
-- Tablas CRM con tenant_id
-- ============================================

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    telefono VARCHAR(20),
    celular VARCHAR(20),
    direccion TEXT,
    ciudad VARCHAR(100),
    pais VARCHAR(100),
    codigo_postal VARCHAR(10),
    identificacion VARCHAR(20),
    tipo_identificacion VARCHAR(20),
    empresa VARCHAR(255),
    cargo VARCHAR(255),
    sector VARCHAR(255),
    notas TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS prospectos (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    telefono VARCHAR(20),
    celular VARCHAR(20),
    empresa VARCHAR(255),
    cargo VARCHAR(255),
    sector VARCHAR(255),
    origen TEXT,
    interes TEXT,
    notas TEXT,
    estado VARCHAR(30) NOT NULL DEFAULT 'NUEVO',
    prioridad VARCHAR(20) DEFAULT 'MEDIA',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    fecha_contacto TIMESTAMP,
    fecha_conversion TIMESTAMP,
    cliente_id BIGINT
);

CREATE TABLE IF NOT EXISTS ventas (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    monto DECIMAL(15,2) NOT NULL,
    descuento DECIMAL(15,2),
    impuesto DECIMAL(15,2),
    total DECIMAL(15,2),
    comision DECIMAL(15,2),
    vendedor VARCHAR(255),
    notas TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    metodo_pago VARCHAR(20) DEFAULT 'TRANSFERENCIA',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP,
    cotizacion_id BIGINT
);

CREATE TABLE IF NOT EXISTS cotizaciones (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    descuento DECIMAL(15,2),
    impuesto DECIMAL(15,2),
    total DECIMAL(15,2) NOT NULL,
    margen DECIMAL(15,2),
    vendedor VARCHAR(255),
    terminos TEXT,
    notas TEXT,
    validez DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    fecha_envio TIMESTAMP,
    fecha_aprobacion TIMESTAMP,
    venta_id BIGINT
);

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    descuento DECIMAL(15,2),
    impuesto DECIMAL(15,2),
    total DECIMAL(15,2) NOT NULL,
    costo_envio DECIMAL(15,2),
    direccion_envio TEXT,
    ciudad_envio VARCHAR(100),
    pais_envio VARCHAR(100),
    codigo_postal_envio VARCHAR(10),
    fecha_entrega_estimada DATE NOT NULL,
    fecha_entrega_real DATE,
    vendedor VARCHAR(255),
    notas TEXT,
    notas_envio TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    metodo_envio VARCHAR(20) DEFAULT 'ESTANDAR',
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    fecha_procesamiento TIMESTAMP,
    fecha_envio TIMESTAMP,
    venta_id BIGINT,
    cotizacion_id BIGINT
);

CREATE TABLE IF NOT EXISTS facturas (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    numero VARCHAR(100),
    descripcion TEXT,
    tipo VARCHAR(50),
    estado VARCHAR(50),
    cliente_id BIGINT,
    venta_id BIGINT,
    fecha_emision DATE,
    fecha_vencimiento DATE,
    fecha_pago DATE,
    subtotal DECIMAL(15,2),
    impuesto DECIMAL(15,2),
    total DECIMAL(15,2),
    moneda VARCHAR(10),
    metodo_pago VARCHAR(50),
    url_factura VARCHAR(500),
    notas TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS contratos (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    codigo VARCHAR(100),
    nombre VARCHAR(255),
    descripcion TEXT,
    tipo VARCHAR(50),
    estado VARCHAR(50),
    cliente_id BIGINT,
    fecha_inicio DATE,
    fecha_fin DATE,
    valor DECIMAL(15,2),
    moneda VARCHAR(10),
    periodo_renovacion VARCHAR(50),
    url_documento VARCHAR(500),
    condiciones TEXT,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS servicios_cliente (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    asunto VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    tipo VARCHAR(30) NOT NULL DEFAULT 'PREGUNTA',
    prioridad VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    canal VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    estado VARCHAR(30) NOT NULL DEFAULT 'ABIERTO',
    asignado_a VARCHAR(255),
    resolucion TEXT,
    notas TEXT,
    fecha_creacion TIMESTAMP NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    fecha_asignacion TIMESTAMP,
    fecha_cierre TIMESTAMP,
    fecha_respuesta TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pqrs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    codigo VARCHAR(100),
    asunto VARCHAR(255),
    descripcion TEXT,
    tipo VARCHAR(50),
    prioridad VARCHAR(50),
    estado VARCHAR(50),
    cliente_id BIGINT,
    canal VARCHAR(50),
    asignado_a VARCHAR(255),
    resolucion TEXT,
    notas TEXT,
    fecha_creacion TIMESTAMP,
    fecha_resolucion TIMESTAMP,
    tiempo_respuesta_horas INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS campanas_marketing (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(255),
    descripcion TEXT,
    tipo VARCHAR(50),
    estado VARCHAR(50),
    fecha_inicio DATE,
    fecha_fin DATE,
    presupuesto DECIMAL(15,2),
    presupuesto_gastado DECIMAL(15,2),
    objetivo VARCHAR(255),
    segmento VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS email_marketing (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    asunto VARCHAR(255),
    contenido TEXT,
    estado VARCHAR(50),
    tipo VARCHAR(50),
    fecha_envio TIMESTAMP,
    fecha_programada TIMESTAMP,
    remitente VARCHAR(255),
    lista_destinatarios TEXT,
    total_enviados INTEGER,
    total_abiertos INTEGER,
    total_clicks INTEGER,
    tasa_apertura DOUBLE PRECISION,
    tasa_click DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS gestion_documental (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(255),
    descripcion TEXT,
    tipo VARCHAR(50),
    categoria VARCHAR(50),
    estado VARCHAR(50),
    url_archivo VARCHAR(500),
    tamano_kb BIGINT,
    extension VARCHAR(20),
    cliente_id BIGINT,
    etiquetas TEXT,
    fecha_subida TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS mesa_ayuda (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    ticket VARCHAR(100),
    asunto VARCHAR(255),
    descripcion TEXT,
    categoria VARCHAR(50),
    prioridad VARCHAR(50),
    estado VARCHAR(50),
    cliente_id BIGINT,
    canal VARCHAR(50),
    asignado_a VARCHAR(255),
    solucion TEXT,
    notas TEXT,
    fecha_creacion TIMESTAMP,
    fecha_cierre TIMESTAMP,
    tiempo_resolucion_minutos INTEGER,
    satisfaccion_cliente INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS encuestas_satisfaccion (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(255),
    descripcion TEXT,
    tipo VARCHAR(50),
    estado VARCHAR(50),
    cliente_id BIGINT,
    fecha_envio TIMESTAMP,
    fecha_respuesta TIMESTAMP,
    calificacion_general INTEGER,
    comentarios TEXT,
    pregunta1 INTEGER,
    pregunta2 INTEGER,
    pregunta3 INTEGER,
    pregunta4 INTEGER,
    pregunta5 INTEGER,
    recomendaria BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS whatsapp_business (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    telefono VARCHAR(20),
    mensaje TEXT,
    estado VARCHAR(50),
    tipo VARCHAR(50),
    fecha_envio TIMESTAMP,
    fecha_programada TIMESTAMP,
    plantilla VARCHAR(255),
    media TEXT,
    leido BOOLEAN,
    respondido BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- ============================================
-- Indices por tenant_id
-- ============================================
CREATE INDEX IF NOT EXISTS idx_usuarios_tenant ON usuarios(tenant_id);
CREATE INDEX IF NOT EXISTS idx_clientes_tenant ON clientes(tenant_id);
CREATE INDEX IF NOT EXISTS idx_prospectos_tenant ON prospectos(tenant_id);
CREATE INDEX IF NOT EXISTS idx_ventas_tenant ON ventas(tenant_id);
CREATE INDEX IF NOT EXISTS idx_cotizaciones_tenant ON cotizaciones(tenant_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_tenant ON pedidos(tenant_id);
CREATE INDEX IF NOT EXISTS idx_facturas_tenant ON facturas(tenant_id);
CREATE INDEX IF NOT EXISTS idx_contratos_tenant ON contratos(tenant_id);
CREATE INDEX IF NOT EXISTS idx_servicios_cliente_tenant ON servicios_cliente(tenant_id);
CREATE INDEX IF NOT EXISTS idx_pqrs_tenant ON pqrs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_campanas_tenant ON campanas_marketing(tenant_id);
CREATE INDEX IF NOT EXISTS idx_email_marketing_tenant ON email_marketing(tenant_id);
CREATE INDEX IF NOT EXISTS idx_gestion_doc_tenant ON gestion_documental(tenant_id);
CREATE INDEX IF NOT EXISTS idx_mesa_ayuda_tenant ON mesa_ayuda(tenant_id);
CREATE INDEX IF NOT EXISTS idx_encuestas_tenant ON encuestas_satisfaccion(tenant_id);
CREATE INDEX IF NOT EXISTS idx_whatsapp_tenant ON whatsapp_business(tenant_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_tenant ON audit_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_tenant ON subscriptions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_billing_invoices_tenant ON billing_invoices(tenant_id);
CREATE INDEX IF NOT EXISTS idx_payments_tenant ON payments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_payment_methods_tenant ON payment_methods(tenant_id);
