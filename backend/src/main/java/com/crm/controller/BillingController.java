package com.crm.controller;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import com.crm.service.BillingService;
import com.crm.service.PlanLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;
    private final BillingInvoiceRepository billingInvoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanLimitService planLimitService;

    @GetMapping("/invoices")
    public ResponseEntity<List<BillingInvoice>> getInvoices() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(billingInvoiceRepository.findByTenantIdOrderByIssueDateDesc(tenantId));
    }

    @GetMapping("/subscription")
    public ResponseEntity<Subscription> getSubscription() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) return ResponseEntity.badRequest().build();
        return subscriptionRepository.findByTenantId(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usage")
    public ResponseEntity<Map<String, Object>> getUsage() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(planLimitService.getUsageStats(tenantId));
    }

    @PostMapping("/invoices/{id}/pay")
    public ResponseEntity<?> payInvoice(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String provider = body.getOrDefault("provider", "MANUAL");
            var payment = billingService.processPayment(id, null, provider);
            billingService.markInvoicePaid(id, payment.getGatewayTransactionId());
            return ResponseEntity.ok(Map.of("status", "PAID", "paymentId", payment.getId()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<BillingInvoice> generateInvoice() {
        Long tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) return ResponseEntity.badRequest().build();
        try {
            return ResponseEntity.ok(billingService.generateInvoice(tenantId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
