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
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByTenantId(Long tenantId);
    List<Commission> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<Commission> findByTenantIdAndStatus(Long tenantId, String status);
    List<Commission> findByTenantIdAndPeriodYearAndPeriodMonth(Long tenantId, Integer year, Integer month);
    Optional<Commission> findByTenantIdAndId(Long tenantId, Long id);
}
