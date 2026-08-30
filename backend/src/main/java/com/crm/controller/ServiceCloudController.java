/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.ServiceCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/service-cloud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceCloudController {

    private final ServiceCloudService service;

    @GetMapping("/knowledge")
    public ResponseEntity<List<KnowledgeArticle>> getArticles(@RequestParam(required = false) String category) { return ResponseEntity.ok(service.getArticles(category)); }

    @GetMapping("/knowledge/published")
    public ResponseEntity<List<KnowledgeArticle>> getPublished() { return ResponseEntity.ok(service.getPublishedArticles()); }

    @PostMapping("/knowledge")
    public ResponseEntity<KnowledgeArticle> createArticle(@RequestBody KnowledgeArticle a) { return ResponseEntity.ok(service.createArticle(a)); }

    @PutMapping("/knowledge/{id}/publish")
    public ResponseEntity<KnowledgeArticle> publish(@PathVariable Long id) { return ResponseEntity.ok(service.publishArticle(id)); }

    @PostMapping("/knowledge/{id}/vote")
    public ResponseEntity<KnowledgeArticle> vote(@PathVariable Long id, @RequestParam boolean helpful) { return ResponseEntity.ok(service.voteArticle(id, helpful)); }

    @GetMapping("/entitlements")
    public ResponseEntity<List<Entitlement>> getEntitlements(@RequestParam(required = false) Long clientId) { return ResponseEntity.ok(service.getEntitlements(clientId)); }

    @PostMapping("/entitlements")
    public ResponseEntity<Entitlement> createEntitlement(@RequestBody Entitlement e) { return ResponseEntity.ok(service.createEntitlement(e)); }

    @GetMapping("/milestones")
    public ResponseEntity<List<ServiceMilestone>> getMilestones(@RequestParam(required = false) Long entitlementId) { return ResponseEntity.ok(service.getMilestones(entitlementId)); }

    @PostMapping("/milestones")
    public ResponseEntity<ServiceMilestone> createMilestone(@RequestBody ServiceMilestone m) { return ResponseEntity.ok(service.createMilestone(m)); }

    @PutMapping("/milestones/{id}/complete")
    public ResponseEntity<ServiceMilestone> completeMilestone(@PathVariable Long id) { return ResponseEntity.ok(service.completeMilestone(id)); }

    @GetMapping("/field-service")
    public ResponseEntity<List<FieldServiceOrder>> getFieldService(@RequestParam(required = false) String status) { return ResponseEntity.ok(service.getFieldServiceOrders(status)); }

    @PostMapping("/field-service")
    public ResponseEntity<FieldServiceOrder> createFieldService(@RequestBody FieldServiceOrder o) { return ResponseEntity.ok(service.createFieldServiceOrder(o)); }

    @PutMapping("/field-service/{id}/status")
    public ResponseEntity<FieldServiceOrder> updateStatus(@PathVariable Long id, @RequestParam String status) { return ResponseEntity.ok(service.updateFieldServiceStatus(id, status)); }

    @GetMapping("/omnichannel")
    public ResponseEntity<Map<String, Object>> getOmnichannel() { return ResponseEntity.ok(service.getOmnichannelRouting()); }
}
