-- Script de inicialización para PostgreSQL
-- Base de datos: crm_db

-- Crear usuario administrador por defecto
INSERT INTO usuarios (username, password, email, nombre, rol, activo, created_at, updated_at) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@crm.com', 'Administrador', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Crear tablas adicionales si es necesario
-- Nota: Hibernate ya crea las tablas automáticamente con spring.jpa.hibernate.ddl-auto=update
