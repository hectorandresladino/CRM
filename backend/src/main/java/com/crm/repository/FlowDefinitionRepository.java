package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, Long> {
    List<FlowDefinition> findByTenantId(Long tenantId);
    List<FlowDefinition> findByTenantIdAndIsActive(Long tenantId, Boolean active);
    List<FlowDefinition> findByTenantIdAndTriggerObject(Long tenantId, String triggerObject);
}
