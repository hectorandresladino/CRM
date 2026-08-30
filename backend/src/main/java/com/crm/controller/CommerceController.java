/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.billing.PaymentProviderFactory;
import com.crm.entity.Tenant;
import com.crm.repository.TenantRepository;
import com.crm.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/commerce")
@RequiredArgsConstructor
public class CommerceController {

    private final SubscriptionLifecycleService subscriptionLifecycleService;
    private final PaymentProviderFactory paymentProviderFactory;
    private final AgencyService agencyService;
    private final SnapshotService snapshotService;
    private final WhiteLabelService whiteLabelService;
    private final TenantRepository tenantRepository;

    @GetMapping("/plans")
    public ResponseEntity<List<Tenant>> getPlans() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }

    @PostMapping("/subscription/upgrade")
    public ResponseEntity<Map<String, Object>> upgrade(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.upgradePlan(
            Long.valueOf(body.get("tenantId").toString()),
            Long.valueOf(body.get("newPlanId").toString()),
            body.getOrDefault("provider", "STRIPE").toString()
        ));
    }

    @PostMapping("/subscription/downgrade")
    public ResponseEntity<Map<String, Object>> downgrade(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.downgradePlan(
            Long.valueOf(body.get("tenantId").toString()),
            Long.valueOf(body.get("newPlanId").toString()),
            body.getOrDefault("provider", "STRIPE").toString()
        ));
    }

    @PostMapping("/subscription/pause")
    public ResponseEntity<Map<String, Object>> pause(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.pauseSubscription(
            Long.valueOf(body.get("tenantId").toString())
        ));
    }

    @PostMapping("/subscription/resume")
    public ResponseEntity<Map<String, Object>> resume(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.resumeSubscription(
            Long.valueOf(body.get("tenantId").toString())
        ));
    }

    @PostMapping("/subscription/cancel")
    public ResponseEntity<Map<String, Object>> cancel(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.cancelSubscription(
            Long.valueOf(body.get("tenantId").toString()),
            body.getOrDefault("reason", "").toString()
        ));
    }

    @GetMapping("/subscription/{tenantId}")
    public ResponseEntity<Map<String, Object>> getSubscription(@PathVariable Long tenantId) {
        return ResponseEntity.ok(subscriptionLifecycleService.getLifecycleStatus(tenantId));
    }

    @PostMapping("/payment/refund")
    public ResponseEntity<Map<String, Object>> refund(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.refundPayment(
            Long.valueOf(body.get("invoiceId").toString()),
            java.math.BigDecimal.valueOf(Double.parseDouble(body.get("amount").toString())),
            body.getOrDefault("provider", "STRIPE").toString()
        ));
    }

    @PostMapping("/payment/retry")
    public ResponseEntity<Map<String, Object>> retryPayment(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(subscriptionLifecycleService.retryPayment(
            Long.valueOf(body.get("invoiceId").toString()),
            Long.valueOf(body.get("paymentMethodId").toString()),
            body.getOrDefault("provider", "STRIPE").toString()
        ));
    }

    @GetMapping("/payment/providers")
    public ResponseEntity<Map<String, Boolean>> getProviders() {
        return ResponseEntity.ok(paymentProviderFactory.getAvailableProviders());
    }

    @PostMapping("/agency/sub-account")
    public ResponseEntity<Tenant> createSubAccount(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(agencyService.createSubAccount(
            Long.valueOf(body.get("agencyTenantId")),
            body.get("name"),
            body.get("slug"),
            body.get("country"),
            body.get("currency")
        ));
    }

    @GetMapping("/agency/{agencyTenantId}/sub-accounts")
    public ResponseEntity<List<Map<String, Object>>> getSubAccounts(@PathVariable Long agencyTenantId) {
        return ResponseEntity.ok(agencyService.getSubAccounts(agencyTenantId));
    }

    @GetMapping("/agency/{agencyTenantId}/dashboard")
    public ResponseEntity<Map<String, Object>> getAgencyDashboard(@PathVariable Long agencyTenantId) {
        return ResponseEntity.ok(agencyService.getAgencyDashboard(agencyTenantId));
    }

    @PostMapping("/agency/suspend")
    public ResponseEntity<Void> suspendSubAccount(@RequestBody Map<String, Object> body) {
        agencyService.suspendSubAccount(
            Long.valueOf(body.get("agencyTenantId").toString()),
            Long.valueOf(body.get("subAccountId").toString()),
            body.getOrDefault("reason", "").toString()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/snapshot/create")
    public ResponseEntity<Map<String, Object>> createSnapshot(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(snapshotService.createSnapshot(
            Long.valueOf(body.get("sourceTenantId").toString())
        ));
    }

    @PostMapping("/snapshot/apply")
    public ResponseEntity<Map<String, Object>> applySnapshot(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(snapshotService.applySnapshot(
            Long.valueOf(body.get("targetTenantId").toString()),
            (Map<String, Object>) body.get("snapshot")
        ));
    }

    @GetMapping("/whitelelabel/{tenantId}")
    public ResponseEntity<Map<String, Object>> getBranding(@PathVariable Long tenantId) {
        return ResponseEntity.ok(whiteLabelService.getBranding(tenantId));
    }

    @PutMapping("/whitelabel/{tenantId}")
    public ResponseEntity<Map<String, Object>> updateBranding(@PathVariable Long tenantId, @RequestBody Map<String, Object> branding) {
        return ResponseEntity.ok(whiteLabelService.updateBranding(tenantId, branding));
    }

    @GetMapping("/whitelabel/public/{domain}")
    public ResponseEntity<Map<String, Object>> getPublicBranding(@PathVariable String domain) {
        return ResponseEntity.ok(whiteLabelService.getPublicBranding(domain));
    }
}
