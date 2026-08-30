/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.GamificationBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamificationBadgeRepository extends JpaRepository<GamificationBadge, Long> {
    List<GamificationBadge> findByTenantId(Long tenantId);
    List<GamificationBadge> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
