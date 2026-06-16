# CRM - Sistema de Gestión de Relaciones con Clientes

Sistema CRM completo con arquitectura de microservicios.

## Stack Tecnológico

- **Frontend**: React 18 + TypeScript + TailwindCSS + shadcn/ui
- **Backend**: Spring Boot 3.x + Java 17
- **Base de Datos**: H2 (desarrollo) / PostgreSQL (producción)
- **Contenedores**: Docker + Docker Compose

## Módulos Principales

1. **Clientes** - Gestión de información de clientes
2. **Prospectos** - Gestión de leads y prospectos
3. **Ventas** - Gestión de oportunidades de venta
4. **Cotizaciones** - Creación y seguimiento de cotizaciones
5. **Pedidos** - Gestión de pedidos de clientes
6. **Servicio al Cliente** - PQRS y soporte
7. **Campañas de Marketing** - Gestión de campañas de marketing
8. **Email Marketing** - Envío y seguimiento de correos masivos
9. **WhatsApp Business** - Mensajería y comunicación por WhatsApp
10. **Gestión Documental** - Almacenamiento y gestión de documentos
11. **Contratos** - Gestión de contratos con clientes
12. **Facturación** - Emisión y seguimiento de facturas
13. **PQRS** - Peticiones, Quejas, Reclamos y Sugerencias
14. **Encuestas de Satisfacción** - Medición de satisfacción del cliente
15. **Mesa de Ayuda** - Sistema de tickets de soporte técnico

## Estructura del Proyecto

```
CRM/
├── backend/          # Spring Boot API
│   ├── Dockerfile
│   └── .dockerignore
├── frontend/         # React Application
│   ├── Dockerfile
│   ├── nginx.conf
│   └── .dockerignore
└── docker-compose.yml # Contenedores completos
```

## Requisitos Previos

- Docker 20+
- Docker Compose 2+

## Despliegue con Docker (Recomendado)

### 1. Iniciar todos los servicios

```bash
docker-compose up -d
```

Esto iniciará:
- **PostgreSQL** en puerto 5432
- **Backend** en puerto 8080
- **Frontend** en puerto 80

### 2. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

### 3. Ver logs

```bash
# Todos los servicios
docker-compose logs -f

# Solo backend
docker-compose logs -f backend

# Solo frontend
docker-compose logs -f frontend
```

### 4. Detener servicios

```bash
docker-compose down
```

### 5. Reconstruir después de cambios

```bash
docker-compose up -d --build
```

## Instalación Local (Desarrollo)

### 1. Base de Datos (PostgreSQL)

```bash
# Usando Docker
docker-compose up postgres -d

# O instalar PostgreSQL localmente y crear la base de datos
createdb crm_db
```

### 2. Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

El API estará disponible en: `http://localhost:8080`

### 3. Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

La aplicación estará disponible en: `http://localhost:5174`

## Configuración

### Backend

Editar `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/crm_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Frontend

Crear archivo `.env` en el directorio `frontend/`:

```env
VITE_API_URL=http://localhost:8080
```

O copiar el archivo de ejemplo:
```bash
cp frontend/.env.example frontend/.env
```

## API Endpoints

- **Clientes**: `/api/clientes`
- **Prospectos**: `/api/prospectos`
- **Ventas**: `/api/ventas`
- **Cotizaciones**: `/api/cotizaciones`
- **Pedidos**: `/api/pedidos`
- **Servicio al Cliente**: `/api/servicio-cliente`
- **Campañas de Marketing**: `/api/campanas-marketing`
- **Email Marketing**: `/api/email-marketing`
- **WhatsApp Business**: `/api/whatsapp-business`
- **Gestión Documental**: `/api/gestion-documental`
- **Contratos**: `/api/contratos`
- **Facturación**: `/api/facturas`
- **PQRS**: `/api/pqrs`
- **Encuestas de Satisfacción**: `/api/encuestas-satisfaccion`
- **Mesa de Ayuda**: `/api/mesa-ayuda`
- **Autenticación**: `/api/auth/login`
- **Dashboard**: `/api/dashboard/stats`

## Desarrollo

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm run dev
```

## Build de Producción

### Backend

```bash
cd backend
mvn clean package
java -jar target/crm-backend-1.0.0.jar
```

### Frontend

```bash
cd frontend
npm run build
```

## Despliegue en Sandbox

Para desplegar en un entorno de sandbox:

1. Subir el código al repositorio
2. El sandbox detectará automáticamente el `docker-compose.yml`
3. Iniciará los contenedores en el orden correcto
4. La aplicación estará disponible en la URL del sandbox

## Licencia

MIT