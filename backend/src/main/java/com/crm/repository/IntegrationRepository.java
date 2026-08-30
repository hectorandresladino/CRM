/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, Long> {
    List<Integration> findByTenantId(Long tenantId);
    Optional<Integration> findByTenantIdAndProvider(Long tenantId, String provider);
    List<Integration> findByTenantIdAndConnected(Long tenantId, Boolean connected);
    List<Integration> findBySyncEnabledTrue();
}
