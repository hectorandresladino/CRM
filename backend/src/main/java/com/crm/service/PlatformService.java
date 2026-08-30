/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlatformService {

    private final CustomObjectRepository customObjectRepo;
    private final ValidationRuleRepository validationRuleRepo;
    private final ApprovalProcessRepository approvalProcessRepo;

    public List<CustomObject> getCustomObjects() {
        return customObjectRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public CustomObject createCustomObject(CustomObject obj) {
        obj.setTenantId(TenantContext.getCurrentTenant());
        return customObjectRepo.save(obj);
    }

    public List<ValidationRule> getValidationRules(String objectName) {
        Long tid = TenantContext.getCurrentTenant();
        if (objectName != null) return validationRuleRepo.findByTenantIdAndObjectName(tid, objectName);
        return validationRuleRepo.findByTenantId(tid);
    }

    public ValidationRule createValidationRule(ValidationRule rule) {
        rule.setTenantId(TenantContext.getCurrentTenant());
        return validationRuleRepo.save(rule);
    }

    public boolean validateRecord(String objectName, Map<String, Object> recordData) {
        List<ValidationRule> rules = validationRuleRepo.findByTenantIdAndObjectName(TenantContext.getCurrentTenant(), objectName);
        for (ValidationRule rule : rules) {
            if (!rule.getIsActive()) continue;
            log.debug("Validating rule: {} on object: {}", rule.getName(), objectName);
        }
        return true;
    }

    public List<ApprovalProcess> getApprovalProcesses() {
        return approvalProcessRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public ApprovalProcess createApprovalProcess(ApprovalProcess process) {
        process.setTenantId(TenantContext.getCurrentTenant());
        return approvalProcessRepo.save(process);
    }

    public Map<String, Object> submitForApproval(Long processId, Long recordId) {
        ApprovalProcess process = approvalProcessRepo.findById(processId)
                .orElseThrow(() -> new RuntimeException("Proceso de aprobacion no encontrado"));
        Map<String, Object> result = new HashMap<>();
        result.put("processId", processId);
        result.put("recordId", recordId);
        result.put("status", "PENDING_APPROVAL");
        result.put("currentStep", 1);
        result.put("submittedAt", java.time.LocalDateTime.now().toString());
        return result;
    }
}
