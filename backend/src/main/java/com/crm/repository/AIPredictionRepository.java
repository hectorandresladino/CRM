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
public interface AIPredictionRepository extends JpaRepository<AIPrediction, Long> {
    List<AIPrediction> findByTenantId(Long tenantId);
    List<AIPrediction> findByTenantIdAndPredictionType(Long tenantId, String type);
    List<AIPrediction> findByTenantIdAndTargetEntityAndTargetId(Long tenantId, String entity, Long id);
    List<AIPrediction> findByTenantIdAndIsActioned(Long tenantId, Boolean actioned);
    Optional<AIPrediction> findByIdAndTenantId(Long id, Long tenantId);
}
