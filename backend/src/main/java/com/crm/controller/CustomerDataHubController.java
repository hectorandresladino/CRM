/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.CustomerDataHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer-data-hub")
@RequiredArgsConstructor
public class CustomerDataHubController {

    private final CustomerDataHubService service;

    @PostMapping("/resolve-identity")
    public ResponseEntity<UnifiedProfile> resolveIdentity(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.resolveIdentity(body.get("email"), body.get("phone"), body.get("name"), body.get("company")));
    }

    @PostMapping("/events")
    public ResponseEntity<CustomerEvent> ingestEvent(@RequestBody CustomerEvent event) { return ResponseEntity.ok(service.ingestEvent(event)); }

    @GetMapping("/events")
    public ResponseEntity<List<CustomerEvent>> getEvents(@RequestParam(required = false) Long clientId, @RequestParam(required = false) String eventType) { return ResponseEntity.ok(service.getEvents(clientId, eventType)); }

    @GetMapping("/profiles")
    public ResponseEntity<List<UnifiedProfile>> getProfiles(@RequestParam(required = false) String lifecycleStage) { return ResponseEntity.ok(service.getProfiles(lifecycleStage)); }

    @PutMapping("/profiles/{id}/lifecycle")
    public ResponseEntity<UnifiedProfile> updateLifecycle(@PathVariable Long id, @RequestParam String stage) { return ResponseEntity.ok(service.updateLifecycleStage(id, stage)); }

    @GetMapping("/profiles/{id}/360")
    public ResponseEntity<Map<String, Object>> getProfile360(@PathVariable Long id) { return ResponseEntity.ok(service.getProfile360(id)); }

    @GetMapping("/segments")
    public ResponseEntity<List<CustomerSegment>> getSegments() { return ResponseEntity.ok(service.getSegments()); }

    @PostMapping("/segments")
    public ResponseEntity<CustomerSegment> createSegment(@RequestBody CustomerSegment s) { return ResponseEntity.ok(service.createSegment(s)); }
}
