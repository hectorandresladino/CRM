/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.AutomationIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/automation")
@RequiredArgsConstructor
public class AutomationIntegrationController {

    private final AutomationIntegrationService service;

    // === Flows (Item 55) ===
    @GetMapping("/flows")
    public ResponseEntity<List<FlowDefinition>> getFlows() { return ResponseEntity.ok(service.getFlows()); }

    @PostMapping("/flows")
    public ResponseEntity<FlowDefinition> createFlow(@RequestBody FlowDefinition flow) { return ResponseEntity.ok(service.createFlow(flow)); }

    @PostMapping("/flows/{id}/activate")
    public ResponseEntity<FlowDefinition> activateFlow(@PathVariable Long id) { return ResponseEntity.ok(service.activateFlow(id)); }

    @PostMapping("/flows/{id}/deactivate")
    public ResponseEntity<FlowDefinition> deactivateFlow(@PathVariable Long id) { return ResponseEntity.ok(service.deactivateFlow(id)); }

    @PostMapping("/flows/{id}/publish")
    public ResponseEntity<FlowDefinition> publishFlow(@PathVariable Long id) { return ResponseEntity.ok(service.publishFlowVersion(id)); }

    // === Flow Execution Logs (Item 56) ===
    @GetMapping("/flows/{flowId}/logs")
    public ResponseEntity<List<FlowExecutionLog>> getFlowLogs(@PathVariable Long flowId) { return ResponseEntity.ok(service.getFlowExecutionLogs(flowId)); }

    @GetMapping("/flows/{flowId}/stats")
    public ResponseEntity<Map<String, Object>> getFlowStats(@PathVariable Long flowId) { return ResponseEntity.ok(service.getFlowExecutionStats(flowId)); }

    // === Workflows (Item 57) ===
    @GetMapping("/workflows")
    public ResponseEntity<List<WorkflowAutomation>> getWorkflows() { return ResponseEntity.ok(service.getWorkflows()); }

    @PostMapping("/workflows")
    public ResponseEntity<WorkflowAutomation> createWorkflow(@RequestBody WorkflowAutomation workflow) { return ResponseEntity.ok(service.createWorkflow(workflow)); }

    @PostMapping("/workflows/{id}/toggle")
    public ResponseEntity<WorkflowAutomation> toggleWorkflow(@PathVariable Long id) { return ResponseEntity.ok(service.toggleWorkflow(id)); }

    @GetMapping("/workflows/stats")
    public ResponseEntity<Map<String, Object>> getWorkflowStats() { return ResponseEntity.ok(service.getWorkflowStats()); }

    // === Webhooks (Item 58) ===
    @GetMapping("/webhooks")
    public ResponseEntity<List<Webhook>> getWebhooks() { return ResponseEntity.ok(service.getWebhooks()); }

    @PostMapping("/webhooks")
    public ResponseEntity<Webhook> createWebhook(@RequestBody Webhook webhook) { return ResponseEntity.ok(service.createWebhook(webhook)); }

    @GetMapping("/webhooks/{id}/logs")
    public ResponseEntity<List<WebhookLog>> getWebhookLogs(@PathVariable Long id) { return ResponseEntity.ok(service.getWebhookLogs(id)); }

    @GetMapping("/webhooks/stats")
    public ResponseEntity<Map<String, Object>> getWebhookStats() { return ResponseEntity.ok(service.getWebhookStats()); }

    // === Integrations (Item 59) ===
    @GetMapping("/integrations")
    public ResponseEntity<List<Integration>> getIntegrations() { return ResponseEntity.ok(service.getIntegrations()); }

    @PostMapping("/integrations")
    public ResponseEntity<Integration> createIntegration(@RequestBody Integration integration) { return ResponseEntity.ok(service.createIntegration(integration)); }

    @PostMapping("/integrations/{id}/connect")
    public ResponseEntity<Integration> connectIntegration(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.connectIntegration(id, body.get("credentials")));
    }

    @PostMapping("/integrations/{id}/disconnect")
    public ResponseEntity<Integration> disconnectIntegration(@PathVariable Long id) { return ResponseEntity.ok(service.disconnectIntegration(id)); }

    @GetMapping("/integrations/{id}/sync-logs")
    public ResponseEntity<List<IntegrationSyncLog>> getSyncLogs(@PathVariable Long id) { return ResponseEntity.ok(service.getSyncLogs(id)); }

    @GetMapping("/integrations/summary")
    public ResponseEntity<Map<String, Object>> getIntegrationSummary() { return ResponseEntity.ok(service.getIntegrationSummary()); }

    // === Scheduled Jobs (Item 60) ===
    @GetMapping("/scheduled-jobs")
    public ResponseEntity<List<ScheduledJob>> getScheduledJobs() { return ResponseEntity.ok(service.getScheduledJobs()); }

    @PostMapping("/scheduled-jobs")
    public ResponseEntity<ScheduledJob> createScheduledJob(@RequestBody ScheduledJob job) { return ResponseEntity.ok(service.createScheduledJob(job)); }

    @PostMapping("/scheduled-jobs/{id}/toggle")
    public ResponseEntity<ScheduledJob> toggleScheduledJob(@PathVariable Long id) { return ResponseEntity.ok(service.toggleScheduledJob(id)); }

    // === Approval Processes (Item 61) ===
    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalProcess>> getApprovals() { return ResponseEntity.ok(service.getApprovalProcesses()); }

    @PostMapping("/approvals")
    public ResponseEntity<ApprovalProcess> createApproval(@RequestBody ApprovalProcess approval) { return ResponseEntity.ok(service.createApprovalProcess(approval)); }

    // === Validation Rules (Item 62) ===
    @GetMapping("/validation-rules")
    public ResponseEntity<List<ValidationRule>> getValidationRules() { return ResponseEntity.ok(service.getValidationRules()); }

    @PostMapping("/validation-rules")
    public ResponseEntity<ValidationRule> createValidationRule(@RequestBody ValidationRule rule) { return ResponseEntity.ok(service.createValidationRule(rule)); }

    // === Dashboard (Item 63) ===
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() { return ResponseEntity.ok(service.getAutomationDashboard()); }

    // === Event Bus (Item 64) ===
    @GetMapping("/event-bus/stats")
    public ResponseEntity<Map<String, Object>> getEventBusStats() { return ResponseEntity.ok(service.getEventBusStats()); }
}
