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
public interface UnifiedProfileRepository extends JpaRepository<UnifiedProfile, Long> {
    List<UnifiedProfile> findByTenantId(Long tenantId);
    Optional<UnifiedProfile> findByTenantIdAndProfileUuid(Long tenantId, String uuid);
    Optional<UnifiedProfile> findByTenantIdAndPrimaryEmail(Long tenantId, String email);
    Optional<UnifiedProfile> findByTenantIdAndPrimaryPhone(Long tenantId, String phone);
    List<UnifiedProfile> findByTenantIdAndLifecycleStage(Long tenantId, String stage);
}
