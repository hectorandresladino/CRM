/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsDashboardRepository extends JpaRepository<AnalyticsDashboard, Long> {
    List<AnalyticsDashboard> findByTenantId(Long tenantId);
    List<AnalyticsDashboard> findByTenantIdAndIsActive(Long tenantId, Boolean active);
    List<AnalyticsDashboard> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
}
