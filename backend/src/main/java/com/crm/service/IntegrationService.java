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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IntegrationService {

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
        Integration configured = integrationRepository.findByTenantIdAndProvider(tenantId, integration.getProvider())
                .orElseGet(Integration::new);
        configured.setTenantId(tenantId);
        configured.setProvider(integration.getProvider());
        configured.setCategory(integration.getCategory());
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
}
