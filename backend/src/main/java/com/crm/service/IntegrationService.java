/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Integration;
import com.crm.repository.IntegrationRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationService {

    private static final Pattern SECRET_FIELD = Pattern.compile(
            "(?i)(api[_-]?key|access[_-]?token|refresh[_-]?token|client[_-]?secret|password|secret)\\s*[\\\"'=:\\s]");

    private final IntegrationRepository integrationRepository;

    public List<Integration> findAll(Long tenantId) {
        Long currentTenant = tid();
        if (!currentTenant.equals(tenantId)) {
            throw new SecurityException("Acceso a otra empresa denegado");
        }
        return integrationRepository.findByTenantId(currentTenant);
    }

    public Integration connect(Integration integration) {
        Long tenantId = tid();
        String provider = normalizeProvider(integration.getProvider());
        String category = normalizeCategory(integration.getCategory());
        validateCategory(provider, category);
        validatePublicConfig(integration.getConfig());
        Integration configured = integrationRepository.findByTenantIdAndProvider(tenantId, provider)
                .orElseGet(Integration::new);
        configured.setTenantId(tenantId);
        configured.setProvider(provider);
        configured.setCategory(category);
        configured.setConfig(integration.getConfig());
        configured.setCredentials(null);
        configured.setConnected(false);
        configured.setSyncEnabled(false);
        configured.setSyncFrequency("MANUAL");
        return integrationRepository.save(configured);
    }

    public Integration disconnect(Long id) {
        Integration integration = findOwned(id);
        integration.setConnected(false);
        integration.setSyncEnabled(false);
        return integrationRepository.save(integration);
    }

    public Integration toggleSync(Long id) {
        Integration integration = findOwned(id);
        if (!Boolean.TRUE.equals(integration.getConnected())) {
            throw new IllegalStateException("La integración no tiene una conexión real configurada");
        }
        integration.setSyncEnabled(!integration.getSyncEnabled());
        return integrationRepository.save(integration);
    }

    public Map<String, Object> testConnection(Long id) {
        Integration integration = findOwned(id);
        Map<String, Object> result = new HashMap<>();
        result.put("provider", integration.getProvider());
        result.put("connected", integration.getConnected());
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("operational", false);
        result.put("status", "CONFIGURATION_ONLY");
        result.put("message", "El conector de " + integration.getProvider() + " aún requiere OAuth/API oficial");
        result.put("requirements", requirementsFor(integration.getProvider()));
        return result;
    }

    public Map<String, Object> syncNow(Long id) {
        Integration integration = findOwned(id);
        throw new UnsupportedOperationException(
                "La sincronización real con " + integration.getProvider() + " aún no está implementada");
    }

    public void delete(Long id) {
        integrationRepository.delete(findOwned(id));
    }

    private Integration findOwned(Long id) {
        return integrationRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Integración no encontrada"));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }

    private String normalizeProvider(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("El proveedor es obligatorio");
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            Integration.Provider.valueOf(normalized);
            return normalized;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Proveedor no soportado: " + value);
        }
    }

    private String normalizeCategory(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("La categoria es obligatoria");
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        try {
            Integration.Category.valueOf(normalized);
            return normalized;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Categoria no soportada: " + value);
        }
    }

    private void validateCategory(String provider, String category) {
        String expected = switch (Integration.Provider.valueOf(provider)) {
            case STRIPE, MERCADO_PAGO -> Integration.Category.PAYMENT.name();
            case GOOGLE_CALENDAR -> Integration.Category.CALENDAR.name();
            case GOOGLE_WORKSPACE, AZURE_AD, OKTA -> Integration.Category.SSO.name();
            case SLACK, WHATSAPP_BUSINESS, META_BUSINESS -> Integration.Category.COMMUNICATION.name();
            case ZAPIER, MAKE -> Integration.Category.AUTOMATION.name();
            case QUICKBOOKS, ALEGRA -> Integration.Category.ACCOUNTING.name();
            case SHOPIFY -> Integration.Category.ECOMMERCE.name();
            case DIAN -> Integration.Category.COMPLIANCE.name();
        };
        if (!expected.equals(category)) {
            throw new IllegalArgumentException("La categoria correcta para " + provider + " es " + expected);
        }
    }

    private void validatePublicConfig(String config) {
        if (config != null && SECRET_FIELD.matcher(config).find()) {
            throw new IllegalArgumentException(
                    "La configuracion publica no puede contener claves, tokens, contrasenas ni secretos");
        }
    }

    private List<String> requirementsFor(String provider) {
        return switch (Integration.Provider.valueOf(provider)) {
            case WHATSAPP_BUSINESS, META_BUSINESS -> List.of("Meta App", "OAuth", "webhook HTTPS publico", "verificacion de firma");
            case GOOGLE_CALENDAR, GOOGLE_WORKSPACE -> List.of("Google Cloud project", "OAuth consent screen", "redirect URI HTTPS");
            case STRIPE, MERCADO_PAGO -> List.of("cuenta del proveedor", "OAuth o clave en secret manager", "webhook firmado");
            case AZURE_AD, OKTA -> List.of("aplicacion SSO", "metadata OIDC/SAML", "redirect URI HTTPS");
            default -> List.of("cuenta del proveedor", "OAuth/API oficial", "secret manager", "webhook firmado si aplica");
        };
    }
}
