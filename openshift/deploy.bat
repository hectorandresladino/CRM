@echo off
echo ========================================
echo   Despliegue CRM SaaS en OpenShift
echo ========================================
echo.

echo [1/9] Verificando login en OpenShift...
oc whoami
if %errorlevel% neq 0 (
    echo Error: No hay sesion activa en OpenShift.
    echo Ejecuta: oc login
    pause
    exit /b 1
)

if not exist openshift\postgresql-secret.env (
    echo Error: falta openshift\postgresql-secret.env
    echo Copia el archivo .env.example y reemplaza todos los valores.
    exit /b 1
)
if not exist openshift\crm-secret.env (
    echo Error: falta openshift\crm-secret.env
    echo Copia el archivo .env.example y reemplaza todos los valores.
    exit /b 1
)
findstr /C:"replace-with" openshift\postgresql-secret.env openshift\crm-secret.env >nul
if %errorlevel% equ 0 (
    echo Error: los archivos de secretos todavia contienen valores de ejemplo.
    exit /b 1
)

echo.
echo [2/9] Creando proyecto CRM (si no existe)...
oc new-project crm --display-name="CRM SaaS" 2>nul || echo Proyecto ya existe, continuando...

echo Creando o actualizando secretos desde archivos locales ignorados por Git...
oc create secret generic postgresql-secret --from-env-file=openshift/postgresql-secret.env --dry-run=client -o yaml | oc apply -f -
if %errorlevel% neq 0 exit /b 1
oc create secret generic crm-secrets --from-env-file=openshift/crm-secret.env --dry-run=client -o yaml | oc apply -f -
if %errorlevel% neq 0 exit /b 1

echo.
echo [3/9] Desplegando PostgreSQL...
oc apply -f openshift/01-postgresql.yaml
echo Esperando a que PostgreSQL este listo...
oc rollout status deployment/postgresql --watch

echo.
echo [4/9] Desplegando Redis...
oc apply -f openshift/06-redis.yaml
echo Esperando a que Redis este listo...
oc rollout status deployment/redis --watch

echo.
echo [5/9] Desplegando MinIO...
oc apply -f openshift/07-minio.yaml
echo Esperando a que MinIO este listo...
oc rollout status deployment/minio --watch

echo.
echo [6/9] Desplegando ConfigMaps...
oc apply -f openshift/05-config-secrets.yaml

echo.
echo [7/9] Desplegando Backend...
oc apply -f openshift/02-backend.yaml
echo Esperando a que el Backend este listo...
oc rollout status deployment/crm-backend --watch

echo.
echo [8/9] Desplegando Frontend y Routes...
oc apply -f openshift/03-frontend.yaml
oc apply -f openshift/04-routes.yaml
echo Esperando a que el Frontend este listo...
oc rollout status deployment/crm-frontend --watch

echo.
echo [9/9] Desplegando HPA y NetworkPolicy...
oc apply -f openshift/08-hpa-networkpolicy.yaml

echo.
echo ========================================
echo   Despliegue SaaS Completado!
echo ========================================
echo.
echo URLs de acceso:
echo   Frontend: oc get route crm-frontend -o jsonpath="{.spec.host}"
echo   API: disponible internamente mediante el proxy /api del frontend
echo.
echo Para ver los logs:
echo   oc logs -f deployment/crm-backend
echo   oc logs -f deployment/crm-frontend
echo   oc logs -f deployment/redis
echo.
pause
