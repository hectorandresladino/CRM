/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.PlatformService;
import com.crm.service.FlowEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlatformController {

    private final PlatformService platformService;
    private final FlowEngineService flowService;

    @GetMapping("/custom-objects")
    public ResponseEntity<List<CustomObject>> getCustomObjects() { return ResponseEntity.ok(platformService.getCustomObjects()); }

    @PostMapping("/custom-objects")
    public ResponseEntity<CustomObject> createCustomObject(@RequestBody CustomObject obj) { return ResponseEntity.ok(platformService.createCustomObject(obj)); }

    @GetMapping("/validation-rules")
    public ResponseEntity<List<ValidationRule>> getValidationRules(@RequestParam(required = false) String objectName) { return ResponseEntity.ok(platformService.getValidationRules(objectName)); }

    @PostMapping("/validation-rules")
    public ResponseEntity<ValidationRule> createValidationRule(@RequestBody ValidationRule rule) { return ResponseEntity.ok(platformService.createValidationRule(rule)); }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateRecord(@RequestParam String objectName, @RequestBody Map<String, Object> data) { return ResponseEntity.ok(platformService.validateRecord(objectName, data)); }

    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalProcess>> getApprovals() { return ResponseEntity.ok(platformService.getApprovalProcesses()); }

    @PostMapping("/approvals")
    public ResponseEntity<ApprovalProcess> createApproval(@RequestBody ApprovalProcess process) { return ResponseEntity.ok(platformService.createApprovalProcess(process)); }

    @PostMapping("/approvals/{processId}/submit")
    public ResponseEntity<Map<String, Object>> submitForApproval(@PathVariable Long processId, @RequestParam Long recordId) { return ResponseEntity.ok(platformService.submitForApproval(processId, recordId)); }

    @GetMapping("/flows")
    public ResponseEntity<List<FlowDefinition>> getFlows() { return ResponseEntity.ok(flowService.getFlows()); }

    @PostMapping("/flows")
    public ResponseEntity<FlowDefinition> createFlow(@RequestBody FlowDefinition flow) { return ResponseEntity.ok(flowService.createFlow(flow)); }

    @PutMapping("/flows/{id}")
    public ResponseEntity<FlowDefinition> updateFlow(@PathVariable Long id, @RequestBody FlowDefinition flow) { return ResponseEntity.ok(flowService.updateFlow(id, flow)); }

    @PostMapping("/flows/{id}/execute")
    public ResponseEntity<Map<String, Object>> executeFlow(@PathVariable Long id, @RequestBody Map<String, Object> context) { return ResponseEntity.ok(flowService.executeFlow(id, context)); }

    @PostMapping("/flows/trigger")
    public ResponseEntity<Map<String, Object>> triggerFlows(@RequestParam String objectName, @RequestParam String triggerType, @RequestBody Map<String, Object> recordData) { return ResponseEntity.ok(flowService.triggerFlowsForObject(objectName, triggerType, recordData)); }

    @PutMapping("/flows/{id}/activate")
    public ResponseEntity<FlowDefinition> activateFlow(@PathVariable Long id) { return ResponseEntity.ok(flowService.activateFlow(id)); }

    @PutMapping("/flows/{id}/deactivate")
    public ResponseEntity<FlowDefinition> deactivateFlow(@PathVariable Long id) { return ResponseEntity.ok(flowService.deactivateFlow(id)); }
}
