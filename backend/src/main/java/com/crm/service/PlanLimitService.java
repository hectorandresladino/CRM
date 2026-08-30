/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanLimitService {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final SubscriptionRepository subscriptionRepository;

    public Plan getPlanForTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant no encontrado"));
        if (tenant.getPlanId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant sin plan asignado");
        }
        return planRepository.findById(tenant.getPlanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan no encontrado"));
    }

    public void checkFeatureEnabled(Long tenantId, String feature) {
        Plan plan = getPlanForTenant(tenantId);
        boolean enabled = switch (feature.toLowerCase()) {
            case "whatsapp" -> Boolean.TRUE.equals(plan.getHasWhatsapp());
            case "email_marketing" -> Boolean.TRUE.equals(plan.getHasEmailMarketing());
            case "api_access" -> Boolean.TRUE.equals(plan.getHasApiAccess());
            case "white_label" -> Boolean.TRUE.equals(plan.getHasWhiteLabel());
            case "ai_features" -> Boolean.TRUE.equals(plan.getHasAiFeatures());
            case "advanced_reports" -> Boolean.TRUE.equals(plan.getHasAdvancedReports());
            case "webhooks" -> Boolean.TRUE.equals(plan.getHasWebhooks());
            default -> true;
        };
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Funcionalidad '" + feature + "' no disponible en el plan " + plan.getName());
        }
    }

    public void checkUserLimit(Long tenantId) {
        // Commercial plans intentionally have unlimited seats. Cost controls
        // are enforced through contacts, storage, messages, AI and API usage.
        getPlanForTenant(tenantId);
    }

    public void checkClientLimit(Long tenantId) {
        Plan plan = getPlanForTenant(tenantId);
        long currentClients = clienteRepository.countByTenantId(tenantId);
        if (plan.getMaxClients() != null && currentClients >= plan.getMaxClients()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "LÃ­mite de clientes alcanzado (" + plan.getMaxClients() + "). Actualice su plan.");
        }
    }

    public void checkAutomationLimit(Long tenantId, long currentAutomations) {
        Plan plan = getPlanForTenant(tenantId);
        if (plan.getMaxAutomations() != null && currentAutomations >= plan.getMaxAutomations()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "LÃ­mite de automatizaciones alcanzado (" + plan.getMaxAutomations() + ").");
        }
    }

    public Map<String, Object> getUsageStats(Long tenantId) {
        Plan plan = getPlanForTenant(tenantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("planName", plan.getName());
        stats.put("maxUsers", null);
        stats.put("unlimitedUsers", true);
        stats.put("currentUsers", usuarioRepository.countByTenantId(tenantId));
        stats.put("maxClients", plan.getMaxClients());
        stats.put("currentClients", clienteRepository.countByTenantId(tenantId));
        stats.put("maxStorageMb", plan.getMaxStorageMb());
        stats.put("maxAutomations", plan.getMaxAutomations());
        stats.put("features", Map.of(
                "whatsapp", Boolean.TRUE.equals(plan.getHasWhatsapp()),
                "emailMarketing", Boolean.TRUE.equals(plan.getHasEmailMarketing()),
                "apiAccess", Boolean.TRUE.equals(plan.getHasApiAccess()),
                "whiteLabel", Boolean.TRUE.equals(plan.getHasWhiteLabel()),
                "aiFeatures", Boolean.TRUE.equals(plan.getHasAiFeatures()),
                "advancedReports", Boolean.TRUE.equals(plan.getHasAdvancedReports()),
                "webhooks", Boolean.TRUE.equals(plan.getHasWebhooks())
        ));
        return stats;
    }

    public boolean isTenantActive(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElse(null);
        if (tenant == null) return false;
        Tenant.TenantStatus status = tenant.getStatus();
        return status == Tenant.TenantStatus.ACTIVE || status == Tenant.TenantStatus.TRIAL;
    }
}
