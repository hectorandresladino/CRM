/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.WorkflowAutomation;
import com.crm.security.TenantContext;
import com.crm.service.WorkflowAutomationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkflowAutomationController {

    private final WorkflowAutomationService workflowAutomationService;

    @GetMapping
    public ResponseEntity<List<WorkflowAutomation>> getAll() {
        return ResponseEntity.ok(workflowAutomationService.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<WorkflowAutomation> create(@RequestBody WorkflowAutomation workflow) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowAutomationService.save(workflow));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowAutomation> update(@PathVariable Long id, @RequestBody WorkflowAutomation workflow) {
        return ResponseEntity.ok(workflowAutomationService.update(id, workflow));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workflowAutomationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<WorkflowAutomation> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(workflowAutomationService.toggle(id));
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

