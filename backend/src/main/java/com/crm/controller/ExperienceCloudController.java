/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.ExperienceCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/experience-cloud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExperienceCloudController {

    private final ExperienceCloudService service;

    @GetMapping
    public ResponseEntity<List<PortalConfig>> getPortals() { return ResponseEntity.ok(service.getPortals()); }

    @PostMapping
    public ResponseEntity<PortalConfig> createPortal(@RequestBody PortalConfig config) { return ResponseEntity.ok(service.createPortal(config)); }

    @PutMapping("/{id}")
    public ResponseEntity<PortalConfig> updatePortal(@PathVariable Long id, @RequestBody PortalConfig config) { return ResponseEntity.ok(service.updatePortal(id, config)); }

    @GetMapping("/{id}/view")
    public ResponseEntity<Map<String, Object>> getPortalView(@PathVariable Long id) { return ResponseEntity.ok(service.getPortalView(id)); }
}
