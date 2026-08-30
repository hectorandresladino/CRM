# CRM SaaS - Sistema Multiempresa de Gestión de Relaciones con Clientes

Plataforma SaaS empresarial multi-tenant para gestión de clientes, ventas, marketing y soporte. Monolito modular construido con Spring Boot + React.

## Stack Tecnológico

- **Frontend**: React 18 + TypeScript + TailwindCSS + Vite + PWA + Capacitor (Android/iOS)
- **Backend**: Spring Boot 3.x + Java 17 + Spring Security + JWT + Redis
- **Base de Datos**: H2 (desarrollo) / PostgreSQL (producción) + Flyway
- **Cache/Sesiones**: Redis
- **Almacenamiento**: MinIO / S3
- **Contenedores**: Docker + Docker Compose + OpenShift
- **CI/CD**: GitHub Actions (build, test, lint, CodeQL, Trivy, OWASP)
- **Observabilidad**: Actuator + Prometheus + Grafana

## Capacidades SaaS

- **Multiempresa (Multi-tenant)**: Aislamiento total por `tenant_id`
- **Seguridad**: JWT, BCrypt, Refresh Token, MFA, bloqueo por intentos, rate limiting
- **Roles**: SUPER_ADMIN, TENANT_OWNER, ADMIN, MANAGER, SALES, MARKETING, SUPPORT, ACCOUNTING
- **Planes**: Starter ($29), Business ($79), Enterprise ($199) y Agency ($399), todos con usuarios internos ilimitados
- **Suscripciones**: Trial, Activa, Vencida, Suspendida, Cancelada con renovación automática
- **Facturación SaaS**: modelo de BillingInvoice, Payment y PaymentMethod; conectores reales de cobro aún pendientes y configurados para fallar de forma segura
- **SuperAdmin**: Gestión de tenants, planes, usuarios globales, métricas SaaS, auditoría
- **Registro automático**: Creación de empresa + trial sin intervención manual
- **Auditoría**: Registro de cambios por usuario, entidad, IP, valor anterior y nuevo
- **Internacionalización**: Español/Inglés, moneda, zona horaria, formato de fecha
- **API v1**: Swagger/OpenAPI, API Keys, Webhooks, versionamiento

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
├── backend/                    # Spring Boot API (Monolito modular SaaS)
│   ├── src/main/java/com/crm/
│   │   ├── config/             # DataInitializer, configuracion
│   │   ├── controller/         # 18 controladores REST + AuthV1 + SuperAdmin
│   │   ├── entity/             # 21 entidades JPA con tenant_id
│   │   │   └── base/           # TenantAware (clase base)
│   │   ├── repository/         # 24 repositorios JPA
│   │   ├── security/           # JWT, TenantContext, SecurityConfig, filtros
│   │   └── service/            # 16 servicios + AuthService SaaS
│   ├── src/main/resources/
│   │   ├── application.properties          # Config base
│   │   ├── application-dev.properties      # Desarrollo (H2)
│   │   ├── application-test.properties     # Testing (H2)
│   │   ├── application-prod.properties     # Produccion (PostgreSQL + Redis)
│   │   └── db/migration/                   # Flyway V1__init_saas_schema.sql
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # React App + PWA + Capacitor
│   ├── src/
│   │   ├── components/         # UI + ProtectedRoute + RoleGuard
│   │   ├── context/            # AuthContext (JWT)
│   │   ├── config/             # API endpoints
│   │   ├── i18n/               # Traducciones ES/EN
│   │   ├── pages/              # 17 paginas + Login + Register
│   │   └── services/           # Axios con interceptor JWT + refresh
│   ├── android/                # Proyecto Capacitor Android
│   ├── Dockerfile
│   └── capacitor.config.ts
├── db/                         # Scripts SQL legacy
│   ├── schema.sql
│   └── init.sql
├── openshift/                  # Manifiestos OpenShift (8 archivos)
│   ├── 01-postgresql.yaml      # PostgreSQL + Secret + PVC
│   ├── 02-backend.yaml         # Backend + ConfigMap + Secrets + probes
│   ├── 03-frontend.yaml        # Frontend
│   ├── 04-routes.yaml          # Routes TLS
│   ├── 05-config-secrets.yaml  # ConfigMaps + Secrets SaaS
│   ├── 06-redis.yaml           # Redis + PVC
│   ├── 07-minio.yaml           # MinIO/S3 + PVC
│   ├── 08-hpa-networkpolicy.yaml # HPA + NetworkPolicy
│   ├── deploy.bat              # Script Windows
│   └── deploy.sh               # Script Linux/Mac
├── .github/workflows/          # CI/CD
│   └── ci-cd.yml               # Build, test, lint, security, docker
├── docker-compose.yml
└── README.md
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
# Usando Docker (recomendado - incluye scripts SQL automáticos)
docker-compose up postgres -d

# Los scripts SQL en db/ se ejecutan automáticamente al iniciar:
# - schema.sql: Crea todas las tablas del sistema
# - init.sql: Inserta datos iniciales (usuario admin)

# O instalar PostgreSQL localmente y crear la base de datos
createdb crm_db
psql crm_db < db/schema.sql
psql crm_db < db/init.sql
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

### Backend - Perfiles por Ambiente

El backend usa Spring Profiles para separar configuración:

| Perfil | BD | Cache | Flyway | Uso |
|--------|-----|-------|--------|-----|
| `dev` (default) | H2 en memoria | - | No | Desarrollo local |
| `test` | H2 en memoria | - | No | Pruebas automatizadas |
| `prod` | PostgreSQL | Redis | Si | Producción / OpenShift |

```bash
# Desarrollo (default)
mvn spring-boot:run

# Produccion
SPRING_PROFILES_ACTIVE=prod \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/crm_saas \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
JWT_SECRET=tu-clave-secreta-min-256-bits \
CORS_ALLOWED_ORIGINS=https://app.tudominio.com \
mvn spring-boot:run
```

### Variables de Entorno (Producción)

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `dev` |
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL | H2 en memoria |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | - |
| `JWT_SECRET` | Clave secreta JWT | clave dev |
| `JWT_ACCESS_EXPIRATION_MS` | Expiración access token | 900000 (15min) |
| `JWT_REFRESH_EXPIRATION_MS` | Expiración refresh token | 604800000 (7días) |
| `CORS_ALLOWED_ORIGINS` | Orígenes web permitidos, separados por coma | obligatorio en `prod` |
| `REDIS_HOST` | Host Redis | `localhost` |
| `REDIS_PORT` | Puerto Redis | `6379` |
| `MINIO_ENDPOINT` | URL MinIO/S3 | `http://minio:9000` |
| `MAIL_HOST` | Host SMTP | `smtp.gmail.com` |
| `MAIL_USERNAME` | Usuario mail | - |
| `MAIL_PASSWORD` | Password mail | - |

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

## Despliegue en Red Hat OpenShift Sandbox

### Archivos de Despliegue

```
openshift/
├── 01-postgresql.yaml    # Secret, PVC, Deployment y Service de PostgreSQL
├── 02-backend.yaml       # ImageStream, BuildConfig, Deployment y Service del Backend
├── 03-frontend.yaml      # ImageStream, BuildConfig, Deployment y Service del Frontend
├── 04-routes.yaml        # Routes con TLS para Frontend y Backend
├── deploy.bat            # Script de despliegue para Windows
└── deploy.sh             # Script de despliegue para Linux/Mac
```

### Despliegue Automático

#### Windows
```cmd
cd c:\Users\User\Desktop\CRM
openshift\deploy.bat
```

#### Linux/Mac
```bash
cd /path/to/CRM
chmod +x openshift/deploy.sh
./openshift/deploy.sh
```

### Despliegue Manual Paso a Paso

1. **Login en OpenShift:**
```bash
oc login https://api.sandbox.openshift.com:6443
```

2. **Crear proyecto:**
```bash
oc new-project crm --display-name="CRM System"
```

3. **Desplegar PostgreSQL:**
```bash
oc apply -f openshift/01-postgresql.yaml
oc rollout status deployment/postgresql --watch
```

4. **Desplegar Backend:**
```bash
oc apply -f openshift/02-backend.yaml
oc rollout status deployment/crm-backend --watch
```

5. **Desplegar Frontend:**
```bash
oc apply -f openshift/03-frontend.yaml
oc rollout status deployment/crm-frontend --watch
```

6. **Crear Routes (URLs públicas):**
```bash
oc apply -f openshift/04-routes.yaml
```

7. **Obtener URLs de acceso:**
```bash
oc get route crm-frontend -o jsonpath='{.spec.host}'
oc get route crm-backend -o jsonpath='{.spec.host}'
```

### Configuración Importante

Antes de desplegar, actualiza la URL del repositorio Git en:
- `openshift/02-backend.yaml` - campo `git.uri`
- `openshift/03-frontend.yaml` - campo `git.uri`

Reemplaza `TU_USUARIO` con tu usuario de GitHub.

### Comandos Útiles

```bash
# Ver pods
oc get pods

# Ver logs
oc logs -f deployment/crm-backend
oc logs -f deployment/crm-frontend

# Ver servicios
oc get svc

# Ver routes
oc get routes

# Escalar backend
oc scale deployment/crm-backend --replicas=2

# Reiniciar despliegue
oc rollout restart deployment/crm-backend
```

## API SaaS v1

### Autenticación

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/auth/register` | POST | Registrar nueva empresa + trial |
| `/api/auth/login` | POST | Iniciar sesión por empresa (devuelve JWT) |
| `/api/v1/auth/refresh` | POST | Renovar access token |
| `/api/v1/auth/logout` | POST | Cerrar sesión (revoca refresh token) |
| `/api/v1/auth/password-reset/request` | POST | Solicitar recuperación de contraseña |
| `/api/v1/auth/password-reset/confirm` | POST | Confirmar nueva contraseña |
| `/api/v1/auth/verify-email` | GET | Verificar correo electrónico |

El login acepta `{ "tenantSlug": "mi-empresa", "username": "usuario", "password": "..." }`.
`tenantSlug` puede omitirse únicamente cuando el nombre de usuario sea inequívoco entre empresas.

### SuperAdmin

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/v1/superadmin/tenants` | GET | Listar todas las empresas |
| `/api/v1/superadmin/tenants/{id}` | GET | Ver empresa específica |
| `/api/v1/superadmin/tenants/{id}/suspend` | PUT | Suspender empresa |
| `/api/v1/superadmin/tenants/{id}/activate` | PUT | Activar empresa |
| `/api/v1/superadmin/plans` | GET/POST | Listar/crear planes |
| `/api/v1/superadmin/subscriptions` | GET | Listar suscripciones |
| `/api/v1/superadmin/users` | GET | Listar usuarios globales |
| `/api/v1/superadmin/metrics` | GET | Métricas SaaS (tenants, revenue, etc.) |
| `/api/v1/superadmin/audit-logs` | GET | Logs de auditoría global |

### Swagger/OpenAPI

Disponible en: `http://localhost:8080/swagger-ui.html`

## Roles y Permisos

| Rol | Descripción | Alcance |
|-----|-------------|---------|
| `SUPER_ADMIN` | Administrador del SaaS | Global (todas las empresas) |
| `TENANT_OWNER` | Dueño de la empresa | Su empresa |
| `ADMIN` | Administrador de empresa | Su empresa |
| `MANAGER` | Gerente | Su empresa (sin gestión de usuarios) |
| `SALES` | Vendedor | Su empresa (solo ventas/prospectos) |
| `MARKETING` | Marketing | Su empresa (solo campañas/email) |
| `SUPPORT` | Soporte | Su empresa (solo PQRS/tickets) |
| `ACCOUNTING` | Contabilidad | Su empresa (solo facturas/contratos) |

## Planes SaaS

| Característica | Starter ($29) | Business ($79) | Enterprise ($199) | Agency ($399) |
|----------------|---------------|----------------|-------------------|---------------|
| Usuarios internos | Ilimitados | Ilimitados | Ilimitados | Ilimitados |
| Contactos | 500 | 5,000 | 50,000 | 100,000 |
| Almacenamiento | 2 GB | 20 GB | 200 GB | 500 GB |
| Subcuentas | - | - | - | 50 |
| Marca blanca | - | - | Sí | Sí |
| Trial | 14 días | 14 días | 30 días | 30 días |

Los usuarios son ilimitados; contactos, almacenamiento, canales, automatizaciones, IA y API se controlan por consumo para mantener una operación sostenible.

## Estado de producto y propiedad intelectual

- [Análisis competitivo, brechas y definición verificable de “100/100”](docs/PRODUCT_GAP_ANALYSIS.md)
- [Estado real de licencia, derechos de autor y pasos de protección](COPYRIGHT.md)
- [Notas obligatorias para migraciones de bases existentes](docs/DATABASE_MIGRATION_NOTES.md)
- [Licencia vigente](LICENSE) y [atribuciones](NOTICE)

Los nombres de proveedores se usan únicamente para identificar integraciones compatibles. No implican afiliación ni patrocinio.

## CI/CD (GitHub Actions)

El pipeline de `.github/workflows/ci-cd.yml` ejecuta:

1. **Backend Build & Test**: Compila, ejecuta tests, empaqueta JAR
2. **Backend Security**: OWASP Dependency Check + CodeQL Analysis
3. **Frontend Build & Lint**: Instala, lint, build
4. **Docker Build**: Construye imágenes backend y frontend
5. **Trivy Scan**: Escaneo de vulnerabilidades en contenedores

## PWA (Progressive Web App)

El frontend está configurado como PWA con las siguientes características:

- **Manifest**: Configurado con `vite-plugin-pwa`
- **Service Worker**: Registro automático con `autoUpdate`
- **Offline**: Cache de recursos estáticos con Workbox
- **Instalable**: Se puede instalar en dispositivos móviles y desktop

### Generar iconos PWA

```bash
cd frontend
npm run build
```

Los iconos se generan automáticamente en `public/icons/`.

## App Store (iOS y Android) con Capacitor

El proyecto está configurado con Capacitor para empaquetar la PWA como aplicación nativa.

### Requisitos

- **iOS**: macOS con Xcode instalado
- **Android**: Android Studio instalado

### Configuración Inicial

```bash
cd frontend
npm install
```

### Agregar Plataformas

```bash
# Para iOS (requiere macOS)
npm run cap:add:ios

# Para Android
npm run cap:add:android
```

### Sincronizar Cambios

Después de modificar el frontend:

```bash
npm run cap:sync
```

### Abrir en IDE Nativo

```bash
# iOS - abre Xcode
npm run cap:open:ios

# Android - abre Android Studio
npm run cap:open:android
```

### Compilar para App Store

#### iOS
1. Abrir Xcode con `npm run cap:open:ios`
2. Configurar certificados de Apple Developer
3. Product > Archive
4. Subir a App Store Connect

#### Android
1. Abrir Android Studio con `npm run cap:open:android`
2. Build > Generate Signed Bundle/APK
3. Firmar con keystore
4. Subir a Google Play Console

### Configuración de appId

- **iOS Bundle ID**: `com.crm.app`
- **Android Package**: `com.crm.app`

Configurado en `frontend/capacitor.config.ts`.

## Licencia

MIT License

Copyright (c) 2024-2026 Hector Andres Ladino

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

See [LICENSE](LICENSE) file for full details.
