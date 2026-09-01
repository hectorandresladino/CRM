# Branch Protection Rules for CRM SaaS

## Configuración Requerida en GitHub

Para proteger la rama `main` y garantizar calidad antes de producción, configure las siguientes reglas en GitHub:

### Pasos para Configurar

1. Vaya a: `Settings` → `Branches` → `Branch protection rules`
2. Haga clic en `Add rule`
3. Nombre de la rama: `main`
4. Configure las siguientes opciones:

### Configuración Sugerida

#### Protección Básica
- ✅ **Require a pull request before merging**
  - ✅ **Require approvals**: 1 aprobación requerida
  - ✅ **Dismiss stale PR approvals when new commits are pushed**
  - ✅ **Require review from Code Owners** (si tiene archivo CODEOWNERS)

#### Restricciones de Commit
- ✅ **Do not allow bypassing the above settings**
- ✅ **Require branches to be up to date before merging**

#### Verificaciones Requeridas
- ✅ **Require status checks to pass before merging**
  - Seleccione los siguientes checks:
    - `backend-build` (Backend Build & Test)
    - `backend-security` (Backend Security Scan)
    - `frontend-build` (Frontend Build & Lint)
  - ✅ **Require branches to be up to date before merging**

#### Opciones Adicionales (Opcionales pero Recomendadas)
- ✅ **Require conversation resolution before merging**
- ✅ **Require linear history**
- ✅ **Allow force pushes** (desmarcar - NO permitir force pushes)
- ✅ **Allow deletions** (desmarcar - NO permitir borrar la rama)

### Archivo CODEOWNERS (Opcional)

Cree un archivo `.github/CODEOWNERS` para definir code owners:

```
# Code Owners para CRM SaaS

# Backend
backend/ @hectorandresladino

# Frontend  
frontend/ @hectorandresladino

# Infraestructura
.github/ @hectorandresladino
.dockerignore @hectorandresladino
Dockerfile* @hectorandresladino

# Configuración
*.yml @hectorandresladino
*.yaml @hectorandresladino
```

### Verificación

Después de configurar:
1. Intente crear un PR a `main`
2. Verifique que los checks de CI/CD sean requeridos
3. Verifique que no pueda hacer push directo a `main`
4. Verifique que no pueda hacer force push a `main`

### Notas

- Los checks de CI/CD deben pasar antes de que el PR pueda ser mergeado
- Al menos 1 aprobación es requerida
- La rama debe estar actualizada con `main` antes de merge
- No se permiten force pushes ni borrado de la rama `main`
