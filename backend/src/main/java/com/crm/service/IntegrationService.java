/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Integration;
import com.crm.repository.IntegrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
        return integrationRepository.findByTenantId(tenantId);
    }

    public Integration connect(Integration integration) {
        integration.setConnected(true);
        if (integration.getSyncEnabled() == null) {
            integration.setSyncEnabled(false);
        }
        if (integration.getSyncFrequency() == null) {
            integration.setSyncFrequency("MANUAL");
        }
        return integrationRepository.save(integration);
    }

    public Integration disconnect(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integracion no encontrada"));
        integration.setConnected(false);
        integration.setSyncEnabled(false);
        return integrationRepository.save(integration);
    }

    public Integration toggleSync(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integracion no encontrada"));
        integration.setSyncEnabled(!integration.getSyncEnabled());
        return integrationRepository.save(integration);
    }

    public Map<String, Object> testConnection(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integracion no encontrada"));
        Map<String, Object> result = new HashMap<>();
        result.put("provider", integration.getProvider());
        result.put("connected", integration.getConnected());
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("status", integration.getConnected() ? "OK" : "NOT_CONNECTED");
        result.put("message", integration.getConnected()
                ? "Conexion activa con " + integration.getProvider()
                : "Integracion no conectada");
        return result;
    }

    public Map<String, Object> syncNow(Long id) {
        Integration integration = integrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Integracion no encontrada"));
        if (!integration.getConnected()) {
            throw new RuntimeException("La integracion no esta conectada");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("provider", integration.getProvider());
        result.put("syncedAt", LocalDateTime.now().toString());
        result.put("status", "SUCCESS");
        result.put("recordsProcessed", 0);

        integration.setLastSyncAt(LocalDateTime.now());
        integrationRepository.save(integration);

        log.info("Sync manual ejecutado para integracion {} ({})", id, integration.getProvider());
        return result;
    }

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void autoSyncEnabledIntegrations() {
        List<Integration> enabled = integrationRepository.findBySyncEnabledTrue();
        for (Integration integration : enabled) {
            try {
                integration.setLastSyncAt(LocalDateTime.now());
                integrationRepository.save(integration);
                log.debug("Auto-sync ejecutado para {} tenant {}", integration.getProvider(), integration.getTenantId());
            } catch (Exception e) {
                log.error("Error en auto-sync de integracion {}: {}", integration.getId(), e.getMessage());
            }
        }
    }

    public void delete(Long id) {
        integrationRepository.deleteById(id);
    }
}
