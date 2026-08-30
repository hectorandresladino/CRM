/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.WorkflowAutomation;
import com.crm.repository.WorkflowAutomationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkflowAutomationService {

    private final WorkflowAutomationRepository workflowAutomationRepository;

    public List<WorkflowAutomation> findAll(Long tenantId) {
        return workflowAutomationRepository.findByTenantId(tenantId);
    }

    public List<WorkflowAutomation> findActive(Long tenantId) {
        return workflowAutomationRepository.findByTenantIdAndActive(tenantId, true);
    }

    public WorkflowAutomation save(WorkflowAutomation workflow) {
        return workflowAutomationRepository.save(workflow);
    }

    public WorkflowAutomation update(Long id, WorkflowAutomation workflow) {
        WorkflowAutomation existing = workflowAutomationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow no encontrado"));
        existing.setName(workflow.getName());
        existing.setDescription(workflow.getDescription());
        existing.setTriggerType(workflow.getTriggerType());
        existing.setTriggerConfig(workflow.getTriggerConfig());
        existing.setActionType(workflow.getActionType());
        existing.setActionConfig(workflow.getActionConfig());
        existing.setActive(workflow.getActive());
        return workflowAutomationRepository.save(existing);
    }

    public void delete(Long id) {
        workflowAutomationRepository.deleteById(id);
    }

    public WorkflowAutomation toggle(Long id) {
        WorkflowAutomation workflow = workflowAutomationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workflow no encontrado"));
        workflow.setActive(!workflow.getActive());
        return workflowAutomationRepository.save(workflow);
    }

    public void recordExecution(Long id) {
        workflowAutomationRepository.findById(id).ifPresent(w -> {
            w.setExecutionCount(w.getExecutionCount() + 1);
            w.setLastExecutedAt(LocalDateTime.now());
            workflowAutomationRepository.save(w);
        });
    }
}
