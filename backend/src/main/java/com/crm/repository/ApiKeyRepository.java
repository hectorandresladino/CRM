/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    List<ApiKey> findByTenantId(Long tenantId);
    Optional<ApiKey> findByKeyAndEsActivo(String key, Boolean esActivo);
    Optional<ApiKey> findByTenantIdAndId(Long tenantId, Long id);
}
