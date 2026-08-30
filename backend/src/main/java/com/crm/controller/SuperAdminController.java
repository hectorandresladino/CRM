/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/superadmin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsuarioRepository usuarioRepository;
    private final BillingInvoiceRepository billingInvoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/tenants")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantRepository.findAll());
    }

    @GetMapping("/tenants/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return tenantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/tenants/{id}/suspend")
    public ResponseEntity<?> suspendTenant(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado"));
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        tenant.setSuspendedReason(body.get("reason"));
        tenantRepository.save(tenant);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tenants/{id}/activate")
    public ResponseEntity<?> activateTenant(@PathVariable Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado"));
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setSuspendedReason(null);
        tenantRepository.save(tenant);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    @PostMapping("/plans")
    public ResponseEntity<Plan> createPlan(@RequestBody Plan plan) {
        return ResponseEntity.ok(planRepository.save(plan));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok(subscriptionRepository.findAll());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Usuario>> getAllUsers() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getSaaSMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTenants", tenantRepository.count());
        metrics.put("activeTenants", tenantRepository.findAll().stream()
                .filter(t -> t.getStatus() == Tenant.TenantStatus.ACTIVE).count());
        metrics.put("trialTenants", tenantRepository.findAll().stream()
                .filter(t -> t.getStatus() == Tenant.TenantStatus.TRIAL).count());
        metrics.put("suspendedTenants", tenantRepository.findAll().stream()
                .filter(t -> t.getStatus() == Tenant.TenantStatus.SUSPENDED).count());
        metrics.put("totalUsers", usuarioRepository.count());
        metrics.put("totalRevenue", paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .mapToDouble(p -> p.getAmount().doubleValue()).sum());
        metrics.put("totalPayments", paymentRepository.count());
        metrics.put("activeSubscriptions", subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == Subscription.SubscriptionStatus.ACTIVE).count());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<BillingInvoice>> getAllInvoices() {
        return ResponseEntity.ok(billingInvoiceRepository.findAll());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @PutMapping("/tenants/{id}/cancel")
    public ResponseEntity<?> cancelTenant(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado"));
        tenant.setStatus(Tenant.TenantStatus.CANCELLED);
        tenant.setSuspendedReason(body.get("reason"));
        tenantRepository.save(tenant);
        subscriptionRepository.findByTenantId(id).ifPresent(sub -> {
            sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
            sub.setCancelledAt(java.time.LocalDateTime.now());
            sub.setCancelReason(body.get("reason"));
            subscriptionRepository.save(sub);
        });
        return ResponseEntity.ok().build();
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id, @RequestBody Plan plan) {
        Plan existing = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        existing.setName(plan.getName());
        existing.setDescription(plan.getDescription());
        existing.setPriceMonthly(plan.getPriceMonthly());
        existing.setPriceYearly(plan.getPriceYearly());
        existing.setMaxUsers(plan.getMaxUsers());
        existing.setMaxClients(plan.getMaxClients());
        existing.setMaxStorageMb(plan.getMaxStorageMb());
        existing.setMaxAutomations(plan.getMaxAutomations());
        existing.setHasWhatsapp(plan.getHasWhatsapp());
        existing.setHasEmailMarketing(plan.getHasEmailMarketing());
        existing.setHasApiAccess(plan.getHasApiAccess());
        existing.setHasWhiteLabel(plan.getHasWhiteLabel());
        existing.setHasAiFeatures(plan.getHasAiFeatures());
        existing.setHasAdvancedReports(plan.getHasAdvancedReports());
        existing.setHasWebhooks(plan.getHasWebhooks());
        existing.setActive(plan.getActive());
        return ResponseEntity.ok(planRepository.save(existing));
    }

    @GetMapping("/tenants/{id}/details")
    public ResponseEntity<Map<String, Object>> getTenantDetails(@PathVariable Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado"));
        Map<String, Object> details = new HashMap<>();
        details.put("tenant", tenant);
        details.put("users", usuarioRepository.findByTenantId(id));
        details.put("subscription", subscriptionRepository.findByTenantId(id).orElse(null));
        details.put("invoices", billingInvoiceRepository.findByTenantIdOrderByIssueDateDesc(id));
        details.put("payments", paymentRepository.findByTenantIdOrderByCreatedAtDesc(id));
        return ResponseEntity.ok(details);
    }
}
