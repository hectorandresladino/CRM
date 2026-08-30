/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.SSOConfiguration;
import com.crm.repository.SSOConfigurationRepository;
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
        return repository.findByTenantId(tenantId);
    }

    public SSOConfiguration save(SSOConfiguration config) {
        return repository.save(config);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public SSOConfiguration toggleActive(Long id) {
        SSOConfiguration config = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConfiguraciÃ³n SSO no encontrada"));
        config.setIsActive(!config.getIsActive());
        return repository.save(config);
    }

    public SSOConfiguration sync(Long id) {
        SSOConfiguration config = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConfiguraciÃ³n SSO no encontrada"));
        config.setLastSyncAt(LocalDateTime.now());
        return repository.save(config);
    }
}
