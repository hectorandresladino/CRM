-- V5__add_core_crm_modules.sql

CREATE TABLE IF NOT EXISTS actividades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    titulo VARCHAR(300) NOT NULL,
    descripcion TEXT,
    cliente_id BIGINT,
    prospecto_id BIGINT,
    venta_id BIGINT,
    asignado_a VARCHAR(100),
    fecha_programada TIMESTAMP,
    fecha_completada TIMESTAMP,
    duracion_minutos INT,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    prioridad VARCHAR(10) DEFAULT 'MEDIA',
    resultado VARCHAR(500),
    recordatorio_minutos INT,
    ubicacion VARCHAR(300),
    participantes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_act_tenant ON actividades(tenant_id);
CREATE INDEX IF NOT EXISTS idx_act_user ON actividades(tenant_id, asignado_a);
CREATE INDEX IF NOT EXISTS idx_act_estado ON actividades(tenant_id, estado);

CREATE TABLE IF NOT EXISTS productos_servicios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    codigo VARCHAR(100) NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    familia VARCHAR(100),
    sub_familia VARCHAR(100),
    tipo VARCHAR(20) NOT NULL,
    precio_base DECIMAL(15, 2) NOT NULL,
    moneda VARCHAR(3) DEFAULT 'USD',
    costo DECIMAL(15, 2),
    impuesto_pct DECIMAL(5, 2),
    descuento_max_pct DECIMAL(5, 2),
    unidad_medida VARCHAR(20) DEFAULT 'unidad',
    stock INT,
    stock_minimo INT,
    es_activo BOOLEAN DEFAULT TRUE,
    es_destacable BOOLEAN DEFAULT FALSE,
    imagen_url VARCHAR(500),
    atributos TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, codigo)
);
CREATE INDEX IF NOT EXISTS idx_prod_tenant ON productos_servicios(tenant_id);

CREATE TABLE IF NOT EXISTS metas_comerciales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    vendedor VARCHAR(100),
    equipo VARCHAR(100),
    periodo VARCHAR(15) NOT NULL,
    anio INT NOT NULL,
    trimestre INT,
    mes INT,
    monto_objetivo DECIMAL(15, 2) NOT NULL,
    monto_alcanzado DECIMAL(15, 2),
    numero_ventas_objetivo INT,
    numero_ventas_real INT,
    porcentaje_cumplimiento DECIMAL(5, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_metas_tenant ON metas_comerciales(tenant_id);

CREATE TABLE IF NOT EXISTS sla_configuraciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100),
    prioridad VARCHAR(10),
    tiempo_respuesta_horas INT,
    tiempo_resolucion_horas INT,
    escalar_a VARCHAR(100),
    horas_desde_escalar INT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sla_tenant ON sla_configuraciones(tenant_id);

CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    factura_id BIGINT,
    cliente_id BIGINT,
    referencia VARCHAR(50) NOT NULL,
    proveedor_pago VARCHAR(20),
    metodo_pago VARCHAR(20),
    monto DECIMAL(15, 2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    transaction_id VARCHAR(200),
    fecha_pago TIMESTAMP,
    fecha_creacion TIMESTAMP,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_pagos_tenant ON pagos(tenant_id);
CREATE INDEX IF NOT EXISTS idx_pagos_estado ON pagos(tenant_id, estado);

CREATE TABLE IF NOT EXISTS webhooks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    evento VARCHAR(100) NOT NULL,
    metodo_http VARCHAR(10) DEFAULT 'POST',
    headers TEXT,
    secret_token VARCHAR(255),
    es_activo BOOLEAN DEFAULT TRUE,
    ultimo_envio TIMESTAMP,
    ultimo_estado INT,
    total_envios INT DEFAULT 0,
    total_exitos INT DEFAULT 0,
    total_fallos INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_webhooks_tenant ON webhooks(tenant_id);

CREATE TABLE IF NOT EXISTS campos_personalizados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    nombre_campo VARCHAR(100) NOT NULL,
    etiqueta VARCHAR(200) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor_defecto VARCHAR(500),
    opciones TEXT,
    es_requerido BOOLEAN DEFAULT FALSE,
    es_busquable BOOLEAN DEFAULT FALSE,
    es_visible_lista BOOLEAN DEFAULT FALSE,
    orden INT DEFAULT 0,
    validacion_regex VARCHAR(500),
    texto_ayuda VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_campos_tenant ON campos_personalizados(tenant_id, entidad);

CREATE TABLE IF NOT EXISTS tenant_configuraciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    color_primario VARCHAR(7),
    color_secundario VARCHAR(7),
    dominio_personalizado VARCHAR(200),
    zona_horaria VARCHAR(50) DEFAULT 'America/Bogota',
    formato_fecha VARCHAR(20) DEFAULT 'DD/MM/YYYY',
    formato_moneda VARCHAR(10) DEFAULT 'es-CO',
    moneda_base VARCHAR(3) DEFAULT 'COP',
    idioma_default VARCHAR(5) DEFAULT 'es',
    prefijo_facturacion VARCHAR(10),
    consecutivo_factura INT DEFAULT 1,
    prefijo_cotizacion VARCHAR(10),
    consecutivo_cotizacion INT DEFAULT 1,
    prefijo_pedido VARCHAR(10),
    consecutivo_pedido INT DEFAULT 1,
    resolucion_facturacion VARCHAR(200),
    nit_empresa VARCHAR(50),
    direccion_empresa VARCHAR(300),
    telefono_empresa VARCHAR(30),
    email_empresa VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS formularios_web (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    campos TEXT NOT NULL,
    destino_prospecto BOOLEAN DEFAULT TRUE,
    destino_cliente BOOLEAN DEFAULT FALSE,
    asignar_a VARCHAR(100),
    mensaje_exito VARCHAR(500),
    redireccion_url VARCHAR(500),
    es_activo BOOLEAN DEFAULT TRUE,
    embed_token VARCHAR(64) NOT NULL UNIQUE,
    total_envios INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_forms_tenant ON formularios_web(tenant_id);

CREATE TABLE IF NOT EXISTS impuestos_configuracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    pais VARCHAR(5) NOT NULL,
    porcentaje DECIMAL(5, 2) NOT NULL,
    tipo_impuesto VARCHAR(20),
    es_incluido BOOLEAN DEFAULT FALSE,
    es_activo BOOLEAN DEFAULT TRUE,
    descripcion VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_imp_tenant ON impuestos_configuracion(tenant_id);

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    key VARCHAR(100) NOT NULL UNIQUE,
    permisos TEXT,
    es_activo BOOLEAN DEFAULT TRUE,
    fecha_expiracion TIMESTAMP,
    ultimo_uso TIMESTAMP,
    total_usos INT DEFAULT 0,
    creado_por VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_apikeys_tenant ON api_keys(tenant_id);

CREATE TABLE IF NOT EXISTS reglas_automaticas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    entidad VARCHAR(50) NOT NULL,
    evento VARCHAR(50) NOT NULL,
    condiciones TEXT NOT NULL,
    acciones TEXT NOT NULL,
    es_activa BOOLEAN DEFAULT TRUE,
    prioridad INT DEFAULT 0,
    total_ejecuciones INT DEFAULT 0,
    ultima_ejecucion TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_reglas_tenant ON reglas_automaticas(tenant_id);
