# Security Policy

## Supported Versions

| Version | Supported          |
|---------|--------------------|
| 5.x     | :white_check_mark: |
| < 5.0   | :x:                |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it responsibly:

1. **DO NOT** open a public GitHub issue
2. Email: security@crm-saas.com with details and reproduction steps
3. You will receive a response within 48 hours
4. We will acknowledge, investigate, and patch if confirmed

## Security Measures

### Authentication
- JWT-based stateless authentication (access + refresh tokens)
- BCrypt(12) password hashing
- Account lockout after 5 failed attempts (15 min)
- Rate limiting: 60 req/min on auth endpoints (IP-based)
- MFA support (TOTP) with recovery codes

### Authorization
- RBAC with 8 roles: SUPER_ADMIN, TENANT_OWNER, ADMIN, MANAGER, SALES, MARKETING, SUPPORT, ACCOUNTING
- Method-level security with @PreAuthorize
- Tenant isolation via TenantStatementInspector (SQL-level filtering)
- Permission Sets and Field-Level Security

### Data Protection
- Multi-tenant isolation at database level (tenant_id on all tables)
- CORS restricted to known origins (no wildcards)
- Security headers: HSTS, X-Content-Type-Options, X-Frame-Options (DENY), XSS-Protection
- Actuator endpoints protected (health/info public, rest SUPER_ADMIN only)
- Input validation on all endpoints

### Infrastructure
- HTTPS enforced in production
- Docker container runs as non-root user
- Secrets via environment variables / Kubernetes secrets
- Database encryption at rest (PostgreSQL)
- Redis for session cache with TTL

## Security Headers

- `Strict-Transport-Security: max-age=31536000; includeSubDomains`
- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1`

## Dependency Scanning

- GitHub Actions CI runs CodeQL analysis on every PR
- Trivy scans Docker images for CVEs
- OWASP Dependency-Check scans Java dependencies
- npm audit scans frontend dependencies
