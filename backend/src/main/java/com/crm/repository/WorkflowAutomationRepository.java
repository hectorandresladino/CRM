package com.crm.repository;

import com.crm.entity.WorkflowAutomation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowAutomationRepository extends JpaRepository<WorkflowAutomation, Long> {
    List<WorkflowAutomation> findByTenantId(Long tenantId);
    List<WorkflowAutomation> findByTenantIdAndActive(Long tenantId, Boolean active);
    List<WorkflowAutomation> findByTenantIdAndTriggerType(Long tenantId, String triggerType);
}
