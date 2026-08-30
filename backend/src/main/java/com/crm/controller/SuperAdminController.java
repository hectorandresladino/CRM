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
}
