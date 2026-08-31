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
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, Long> {
    List<FlowDefinition> findByTenantId(Long tenantId);
    List<FlowDefinition> findByTenantIdAndIsActive(Long tenantId, Boolean active);
    List<FlowDefinition> findByTenantIdAndTriggerObject(Long tenantId, String triggerObject);
    Optional<FlowDefinition> findByTenantIdAndId(Long tenantId, Long id);
}
