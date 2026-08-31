/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerritoryRepository extends JpaRepository<Territory, Long> {
    List<Territory> findByTenantId(Long tenantId);
    List<Territory> findByTenantIdAndActive(Long tenantId, Boolean active);
    List<Territory> findByTenantIdAndParentId(Long tenantId, Long parentId);
    Optional<Territory> findByTenantIdAndId(Long tenantId, Long id);
}
