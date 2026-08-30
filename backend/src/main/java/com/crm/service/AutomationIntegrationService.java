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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AutomationIntegrationService {

    private final FlowDefinitionRepository flowRepo;
    private final FlowExecutionLogRepository flowLogRepo;
    private final WorkflowAutomationRepository workflowRepo;
    private final WebhookRepository webhookRepo;
    private final WebhookLogRepository webhookLogRepo;
    private final IntegrationRepository integrationRepo;
    private final IntegrationSyncLogRepository syncLogRepo;
    private final ScheduledJobRepository scheduledJobRepo;
    private final ApprovalProcessRepository approvalRepo;
    private final ValidationRuleRepository validationRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Flow Builder (Item 55) ===

    public List<FlowDefinition> getFlows() { return flowRepo.findByTenantId(tid()); }

    public FlowDefinition createFlow(FlowDefinition flow) {
        flow.setTenantId(tid());
        return flowRepo.save(flow);
    }

    public FlowDefinition activateFlow(Long id) {
        FlowDefinition flow = flowRepo.findById(id).orElseThrow();
        flow.setIsActive(true);
        return flowRepo.save(flow);
    }

    public FlowDefinition deactivateFlow(Long id) {
        FlowDefinition flow = flowRepo.findById(id).orElseThrow();
        flow.setIsActive(false);
        return flowRepo.save(flow);
    }

    public FlowDefinition publishFlowVersion(Long id) {
        FlowDefinition flow = flowRepo.findById(id).orElseThrow();
        flow.setVersion(flow.getVersion() + 1);
        return flowRepo.save(flow);
    }

    // === Flow Execution Logs (Item 56) ===

    public FlowExecutionLog logFlowExecution(FlowExecutionLog logEntry) {
        logEntry.setTenantId(tid());
        return flowLogRepo.save(logEntry);
    }

    public List<FlowExecutionLog> getFlowExecutionLogs(Long flowId) {
        return flowLogRepo.findByTenantIdAndFlowId(tid(), flowId);
    }

    public Map<String, Object> getFlowExecutionStats(Long flowId) {
        List<FlowExecutionLog> logs = flowLogRepo.findByTenantIdAndFlowId(tid(), flowId);
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalExecutions", logs.size());
        stats.put("completed", logs.stream().filter(l -> l.getStatus() == FlowExecutionLog.ExecutionStatus.COMPLETED).count());
        stats.put("failed", logs.stream().filter(l -> l.getStatus() == FlowExecutionLog.ExecutionStatus.FAILED).count());
        stats.put("running", logs.stream().filter(l -> l.getStatus() == FlowExecutionLog.ExecutionStatus.RUNNING).count());
        double avgDuration = logs.stream()
                .filter(l -> l.getDurationMs() != null)
                .mapToLong(FlowExecutionLog::getDurationMs)
                .average().orElse(0);
        stats.put("avgDurationMs", Math.round(avgDuration));
        stats.put("successRate", logs.size() > 0
                ? (double) logs.stream().filter(l -> l.getStatus() == FlowExecutionLog.ExecutionStatus.COMPLETED).count() / logs.size() * 100 : 0);
        return stats;
    }

    // === Workflow Automation (Item 57) ===

    public List<WorkflowAutomation> getWorkflows() { return workflowRepo.findByTenantId(tid()); }

    public WorkflowAutomation createWorkflow(WorkflowAutomation workflow) {
        workflow.setTenantId(tid());
        return workflowRepo.save(workflow);
    }

    public WorkflowAutomation toggleWorkflow(Long id) {
        WorkflowAutomation wf = workflowRepo.findById(id).orElseThrow();
        wf.setActive(!wf.getActive());
        return workflowRepo.save(wf);
    }

    public Map<String, Object> getWorkflowStats() {
        List<WorkflowAutomation> wfs = workflowRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalWorkflows", wfs.size());
        stats.put("activeWorkflows", wfs.stream().filter(WorkflowAutomation::getActive).count());
        int totalExecutions = wfs.stream()
                .mapToInt(w -> w.getExecutionCount() != null ? w.getExecutionCount() : 0)
                .sum();
        stats.put("totalExecutions", totalExecutions);

        Map<String, Long> byTrigger = new LinkedHashMap<>();
        for (WorkflowAutomation wf : wfs) {
            String trigger = wf.getTriggerType() != null ? wf.getTriggerType() : "UNKNOWN";
            byTrigger.merge(trigger, 1L, Long::sum);
        }
        stats.put("byTriggerType", byTrigger);

        Map<String, Long> byAction = new LinkedHashMap<>();
        for (WorkflowAutomation wf : wfs) {
            String action = wf.getActionType() != null ? wf.getActionType() : "UNKNOWN";
            byAction.merge(action, 1L, Long::sum);
        }
        stats.put("byActionType", byAction);
        return stats;
    }

    // === Webhooks (Item 58) ===

    public List<Webhook> getWebhooks() { return webhookRepo.findByTenantId(tid()); }

    public Webhook createWebhook(Webhook webhook) {
        webhook.setTenantId(tid());
        if (webhook.getSecretToken() == null || webhook.getSecretToken().isBlank()) {
            webhook.setSecretToken(UUID.randomUUID().toString());
        }
        return webhookRepo.save(webhook);
    }

    public WebhookLog logWebhookDelivery(WebhookLog logEntry) {
        logEntry.setTenantId(tid());
        return webhookLogRepo.save(logEntry);
    }

    public List<WebhookLog> getWebhookLogs(Long webhookId) {
        return webhookLogRepo.findByTenantIdAndWebhookId(tid(), webhookId);
    }

    public Map<String, Object> getWebhookStats() {
        List<Webhook> webhooks = webhookRepo.findByTenantId(tid());
        List<WebhookLog> allLogs = webhookLogRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalWebhooks", webhooks.size());
        stats.put("activeWebhooks", webhooks.stream().filter(Webhook::getEsActivo).count());
        stats.put("totalDeliveries", allLogs.size());
        stats.put("delivered", allLogs.stream().filter(l -> l.getStatus() == WebhookLog.DeliveryStatus.DELIVERED).count());
        stats.put("failed", allLogs.stream().filter(l -> l.getStatus() == WebhookLog.DeliveryStatus.FAILED).count());
        stats.put("deliveryRate", allLogs.size() > 0
                ? (double) allLogs.stream().filter(l -> l.getStatus() == WebhookLog.DeliveryStatus.DELIVERED).count() / allLogs.size() * 100 : 0);
        return stats;
    }

    // === Integration Management (Item 59) ===

    public List<Integration> getIntegrations() { return integrationRepo.findByTenantId(tid()); }

    public Integration createIntegration(Integration integration) {
        integration.setTenantId(tid());
        return integrationRepo.save(integration);
    }

    public Integration connectIntegration(Long id, String credentials) {
        Integration integration = integrationRepo.findById(id).orElseThrow();
        integration.setConnected(true);
        integration.setCredentials(credentials);
        integration.setSyncEnabled(true);
        integration.setLastSyncAt(LocalDateTime.now());
        return integrationRepo.save(integration);
    }

    public Integration disconnectIntegration(Long id) {
        Integration integration = integrationRepo.findById(id).orElseThrow();
        integration.setConnected(false);
        integration.setSyncEnabled(false);
        integration.setCredentials(null);
        return integrationRepo.save(integration);
    }

    public IntegrationSyncLog logSync(IntegrationSyncLog logEntry) {
        logEntry.setTenantId(tid());
        return syncLogRepo.save(logEntry);
    }

    public List<IntegrationSyncLog> getSyncLogs(Long integrationId) {
        return syncLogRepo.findByTenantIdAndIntegrationId(tid(), integrationId);
    }

    public Map<String, Object> getIntegrationSummary() {
        List<Integration> integrations = integrationRepo.findByTenantId(tid());
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIntegrations", integrations.size());
        summary.put("connected", integrations.stream().filter(Integration::getConnected).count());
        summary.put("syncEnabled", integrations.stream().filter(Integration::getSyncEnabled).count());

        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (Integration i : integrations) {
            String cat = i.getCategory() != null ? i.getCategory() : "UNKNOWN";
            byCategory.merge(cat, 1L, Long::sum);
        }
        summary.put("byCategory", byCategory);

        Map<String, Long> byProvider = new LinkedHashMap<>();
        for (Integration i : integrations) {
            String prov = i.getProvider() != null ? i.getProvider() : "UNKNOWN";
            byProvider.merge(prov, 1L, Long::sum);
        }
        summary.put("byProvider", byProvider);
        return summary;
    }

    // === Scheduled Jobs (Item 60) ===

    public List<ScheduledJob> getScheduledJobs() { return scheduledJobRepo.findByTenantId(tid()); }

    public ScheduledJob createScheduledJob(ScheduledJob job) {
        job.setTenantId(tid());
        job.setNextRunAt(calculateNextRun(job.getCronExpression()));
        return scheduledJobRepo.save(job);
    }

    public ScheduledJob toggleScheduledJob(Long id) {
        ScheduledJob job = scheduledJobRepo.findById(id).orElseThrow();
        job.setIsActive(!job.getIsActive());
        if (job.getIsActive()) {
            job.setNextRunAt(calculateNextRun(job.getCronExpression()));
        } else {
            job.setNextRunAt(null);
        }
        return scheduledJobRepo.save(job);
    }

    public ScheduledJob recordJobExecution(Long id, boolean success, String error) {
        ScheduledJob job = scheduledJobRepo.findById(id).orElseThrow();
        job.setRunCount(job.getRunCount() + 1);
        job.setLastRunAt(LocalDateTime.now());
        if (!success) {
            job.setFailureCount(job.getFailureCount() + 1);
            job.setLastError(error);
        }
        job.setNextRunAt(calculateNextRun(job.getCronExpression()));
        return scheduledJobRepo.save(job);
    }

    private LocalDateTime calculateNextRun(String cron) {
        return LocalDateTime.now().plusHours(1);
    }

    // === Approval Processes (Item 61) ===

    public List<ApprovalProcess> getApprovalProcesses() { return approvalRepo.findByTenantId(tid()); }

    public ApprovalProcess createApprovalProcess(ApprovalProcess approval) {
        approval.setTenantId(tid());
        return approvalRepo.save(approval);
    }

    // === Validation Rules (Item 62) ===

    public List<ValidationRule> getValidationRules() { return validationRepo.findByTenantId(tid()); }

    public ValidationRule createValidationRule(ValidationRule rule) {
        rule.setTenantId(tid());
        return validationRepo.save(rule);
    }

    // === Automation Dashboard (Item 63) ===

    public Map<String, Object> getAutomationDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("activeFlows", flowRepo.findByTenantId(tid()).stream().filter(FlowDefinition::getIsActive).count());
        dashboard.put("activeWorkflows", workflowRepo.findByTenantId(tid()).stream().filter(WorkflowAutomation::getActive).count());
        dashboard.put("activeWebhooks", webhookRepo.findByTenantId(tid()).stream().filter(Webhook::getEsActivo).count());
        dashboard.put("connectedIntegrations", integrationRepo.findByTenantId(tid()).stream().filter(Integration::getConnected).count());
        dashboard.put("activeScheduledJobs", scheduledJobRepo.findByTenantIdAndIsActive(tid(), true).size());
        dashboard.put("approvalProcesses", approvalRepo.findByTenantId(tid()).size());
        dashboard.put("validationRules", validationRepo.findByTenantId(tid()).size());
        dashboard.put("workflowStats", getWorkflowStats());
        dashboard.put("webhookStats", getWebhookStats());
        dashboard.put("integrationSummary", getIntegrationSummary());
        return dashboard;
    }

    // === Event Bus (Item 64) ===

    public Map<String, Object> getEventBusStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<FlowExecutionLog> flowLogs = flowLogRepo.findByTenantId(tid());
        List<WebhookLog> webhookLogs = webhookLogRepo.findByTenantId(tid());

        Map<String, Long> eventsByType = new LinkedHashMap<>();
        for (FlowExecutionLog l : flowLogs) {
            String type = l.getTriggerType() != null ? l.getTriggerType() : "UNKNOWN";
            eventsByType.merge(type, 1L, Long::sum);
        }
        for (WebhookLog l : webhookLogs) {
            String type = "WEBHOOK_" + (l.getEventType() != null ? l.getEventType() : "UNKNOWN");
            eventsByType.merge(type, 1L, Long::sum);
        }
        stats.put("eventsByType", eventsByType);
        stats.put("totalFlowEvents", flowLogs.size());
        stats.put("totalWebhookEvents", webhookLogs.size());
        stats.put("totalEvents", flowLogs.size() + webhookLogs.size());

        long pendingFlows = flowLogs.stream().filter(l -> l.getStatus() == FlowExecutionLog.ExecutionStatus.RUNNING).count();
        long pendingWebhooks = webhookLogs.stream().filter(l -> l.getStatus() == WebhookLog.DeliveryStatus.PENDING
                || l.getStatus() == WebhookLog.DeliveryStatus.RETRYING).count();
        stats.put("pendingEvents", pendingFlows + pendingWebhooks);

        return stats;
    }
}
