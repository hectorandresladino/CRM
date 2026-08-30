/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.service.DevOpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/devops")
@RequiredArgsConstructor
public class DevOpsController {

    private final DevOpsService service;

    @GetMapping("/feature-flags")
    public ResponseEntity<Map<String, Object>> getFeatureFlags() { return ResponseEntity.ok(service.getFeatureFlags()); }

    @PutMapping("/feature-flags/{feature}")
    public ResponseEntity<Map<String, Object>> toggleFlag(@PathVariable String feature, @RequestParam boolean enabled) { return ResponseEntity.ok(service.toggleFeatureFlag(feature, enabled)); }

    @GetMapping("/sandbox")
    public ResponseEntity<Map<String, Object>> getSandbox() { return ResponseEntity.ok(service.getSandboxInfo()); }

    @GetMapping("/release")
    public ResponseEntity<Map<String, Object>> getRelease() { return ResponseEntity.ok(service.getReleaseInfo()); }

    @PostMapping("/rollback/{version}")
    public ResponseEntity<Map<String, Object>> rollback(@PathVariable String version) { return ResponseEntity.ok(service.rollbackMigration(version)); }

    @GetMapping("/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() { return ResponseEntity.ok(service.getSystemHealth()); }
}
