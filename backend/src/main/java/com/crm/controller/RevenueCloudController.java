/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.RevenueCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/revenue-cloud")
@RequiredArgsConstructor
public class RevenueCloudController {

    private final RevenueCloudService service;

    @PostMapping("/amendments")
    public ResponseEntity<SubscriptionAmendment> createAmendment(@RequestBody SubscriptionAmendment a) { return ResponseEntity.ok(service.createAmendment(a)); }

    @PutMapping("/amendments/{id}/process")
    public ResponseEntity<SubscriptionAmendment> processAmendment(@PathVariable Long id) { return ResponseEntity.ok(service.processAmendment(id)); }

    @GetMapping("/amendments")
    public ResponseEntity<List<SubscriptionAmendment>> getAmendments(@RequestParam(required = false) Long subscriptionId) {
        if (subscriptionId != null) return ResponseEntity.ok(service.getSubscriptionLifecycle(subscriptionId).containsKey("amendments") ? (List<SubscriptionAmendment>) service.getSubscriptionLifecycle(subscriptionId).get("amendments") : List.of());
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/usage")
    public ResponseEntity<UsageRecord> recordUsage(@RequestBody UsageRecord r) { return ResponseEntity.ok(service.recordUsage(r)); }

    @GetMapping("/usage")
    public ResponseEntity<List<UsageRecord>> getUsage(@RequestParam(required = false) Long subscriptionId) { return ResponseEntity.ok(service.getUsageRecords(subscriptionId)); }

    @GetMapping("/usage/unbilled")
    public ResponseEntity<List<UsageRecord>> getUnbilledUsage() { return ResponseEntity.ok(service.getUnbilledUsage()); }

    @PostMapping("/dunning")
    public ResponseEntity<DunningCampaign> createDunning(@RequestBody DunningCampaign d) { return ResponseEntity.ok(service.createDunningStep(d)); }

    @GetMapping("/dunning")
    public ResponseEntity<List<DunningCampaign>> getDunning(@RequestParam(required = false) Long invoiceId) { return ResponseEntity.ok(service.getDunningCampaigns(invoiceId)); }

    @PutMapping("/dunning/{id}/send")
    public ResponseEntity<DunningCampaign> sendDunning(@PathVariable Long id) { return ResponseEntity.ok(service.sendDunningStep(id)); }

    @GetMapping("/lifecycle/{subscriptionId}")
    public ResponseEntity<Map<String, Object>> getLifecycle(@PathVariable Long subscriptionId) { return ResponseEntity.ok(service.getSubscriptionLifecycle(subscriptionId)); }
}
