/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ApiKey;
import com.crm.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ApiKeyService {
    private final ApiKeyRepository repository;

    public List<ApiKey> findAll(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    public ApiKey create(ApiKey apiKey) {
        apiKey.setKey("crm_" + UUID.randomUUID().toString().replace("-", ""));
        return repository.save(apiKey);
    }

    public boolean validate(String key) {
        return repository.findByKeyAndEsActivo(key, true).map(k -> {
            k.setUltimoUso(LocalDateTime.now());
            k.setTotalUsos(k.getTotalUsos() + 1);
            repository.save(k);
            return true;
        }).orElse(false);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
