/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.SSOConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SSOConfigurationRepository extends JpaRepository<SSOConfiguration, Long> {
    List<SSOConfiguration> findByTenantId(Long tenantId);
    Optional<SSOConfiguration> findByTenantIdAndProvider(Long tenantId, String provider);
    Optional<SSOConfiguration> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
