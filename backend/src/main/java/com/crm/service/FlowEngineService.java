package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FlowEngineService {

    private final FlowDefinitionRepository flowRepo;

    public List<FlowDefinition> getFlows() {
        return flowRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public FlowDefinition createFlow(FlowDefinition flow) {
        flow.setTenantId(TenantContext.getCurrentTenant());
        return flowRepo.save(flow);
    }

    public FlowDefinition updateFlow(Long id, FlowDefinition updated) {
        FlowDefinition flow = flowRepo.findById(id).orElseThrow(() -> new RuntimeException("Flow no encontrado"));
        flow.setName(updated.getName());
        flow.setDescription(updated.getDescription());
        flow.setFlowType(updated.getFlowType());
        flow.setTriggerType(updated.getTriggerType());
        flow.setTriggerObject(updated.getTriggerObject());
        flow.setTriggerCondition(updated.getTriggerCondition());
        flow.setFlowSteps(updated.getFlowSteps());
        flow.setVersion(flow.getVersion() + 1);
        return flowRepo.save(flow);
    }

    public Map<String, Object> executeFlow(Long flowId, Map<String, Object> context) {
        FlowDefinition flow = flowRepo.findById(flowId)
                .orElseThrow(() -> new RuntimeException("Flow no encontrado"));
        if (!flow.getIsActive()) throw new RuntimeException("Flow inactivo");

        flow.setRunCount(flow.getRunCount() + 1);
        flow.setLastRunAt(LocalDateTime.now());
        flowRepo.save(flow);

        Map<String, Object> result = new HashMap<>();
        result.put("flowId", flowId);
        result.put("flowName", flow.getName());
        result.put("status", "COMPLETED");
        result.put("executedAt", LocalDateTime.now().toString());
        result.put("stepsExecuted", 0);
        result.put("context", context);
        log.info("Flow {} ejecutado para tenant {}", flow.getName(), flow.getTenantId());
        return result;
    }

    public Map<String, Object> triggerFlowsForObject(String objectName, String triggerType, Map<String, Object> recordData) {
        Long tid = TenantContext.getCurrentTenant();
        List<FlowDefinition> flows = flowRepo.findByTenantIdAndTriggerObject(tid, objectName);
        List<Map<String, Object>> results = new ArrayList<>();
        for (FlowDefinition flow : flows) {
            if (!flow.getIsActive()) continue;
            if (flow.getTriggerType() != null && flow.getTriggerType().equals(triggerType)) {
                results.add(executeFlow(flow.getId(), recordData));
            }
        }
        Map<String, Object> summary = new HashMap<>();
        summary.put("triggeredFlows", results.size());
        summary.put("results", results);
        return summary;
    }

    public FlowDefinition activateFlow(Long id) {
        FlowDefinition f = flowRepo.findById(id).orElseThrow(() -> new RuntimeException("Flow no encontrado"));
        f.setIsActive(true);
        return flowRepo.save(f);
    }

    public FlowDefinition deactivateFlow(Long id) {
        FlowDefinition f = flowRepo.findById(id).orElseThrow(() -> new RuntimeException("Flow no encontrado"));
        f.setIsActive(false);
        return flowRepo.save(f);
    }
}
