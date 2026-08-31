/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.SSOConfiguration;
import com.crm.repository.SSOConfigurationRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SSOService {

    private final SSOConfigurationRepository repository;

    public List<SSOConfiguration> findAll(Long tenantId) {
        return repository.findByTenantId(tid());
    }

    public SSOConfiguration save(SSOConfiguration config) {
        config.setTenantId(tid());
        return repository.save(config);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Configuración SSO no encontrada")));
    }

    public SSOConfiguration toggleActive(Long id) {
        SSOConfiguration config = repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Configuración SSO no encontrada"));
        config.setIsActive(!config.getIsActive());
        return repository.save(config);
    }

    public SSOConfiguration sync(Long id) {
        SSOConfiguration config = repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Configuración SSO no encontrada"));
        config.setLastSyncAt(LocalDateTime.now());
        return repository.save(config);
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
