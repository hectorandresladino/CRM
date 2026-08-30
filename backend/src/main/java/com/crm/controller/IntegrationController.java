/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Integration;
import com.crm.security.TenantContext;
import com.crm.service.IntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IntegrationController {

    private final IntegrationService integrationService;

    @GetMapping
    public ResponseEntity<List<Integration>> getAll() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) tenantId = 1L;
        return ResponseEntity.ok(integrationService.findAll(tenantId));
    }

    @PostMapping
    public ResponseEntity<Integration> connect(@RequestBody Integration integration) {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) integration.setTenantId(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(integrationService.connect(integration));
    }

    @PatchMapping("/{id}/disconnect")
    public ResponseEntity<Integration> disconnect(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.disconnect(id));
    }

    @PatchMapping("/{id}/toggle-sync")
    public ResponseEntity<Integration> toggleSync(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.toggleSync(id));
    }

    @GetMapping("/{id}/test")
    public ResponseEntity<Map<String, Object>> testConnection(@PathVariable Long id) {
        return ResponseEntity.ok(integrationService.testConnection(id));
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<Map<String, Object>> syncNow(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(integrationService.syncNow(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        integrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
