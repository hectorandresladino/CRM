/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.billing.PaymentProvider;
import com.crm.billing.PaymentProvider.PaymentRequest;
import com.crm.billing.PaymentProvider.PaymentResult;
import com.crm.billing.PaymentProviderFactory;
import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionLifecycleService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final TenantRepository tenantRepository;
    private final BillingInvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentProviderFactory providerFactory;

    public Map<String, Object> upgradePlan(Long tenantId, Long newPlanId, String providerName) {
        return changePlan(tenantId, newPlanId, providerName, true);
    }

    public Map<String, Object> downgradePlan(Long tenantId, Long newPlanId, String providerName) {
        return changePlan(tenantId, newPlanId, providerName, false);
    }

    private Map<String, Object> changePlan(Long tenantId, Long newPlanId, String providerName, boolean isUpgrade) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin suscripcion activa"));
        Plan oldPlan = planRepository.findById(sub.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan actual no encontrado"));
        Plan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan nuevo no encontrado"));

        BigDecimal proration = calculateProration(sub, oldPlan, newPlan);

        if (isUpgrade && proration.compareTo(BigDecimal.ZERO) > 0) {
            PaymentProvider provider = providerFactory.getProvider(providerName);
            PaymentResult result = provider.charge(new PaymentRequest(
                String.valueOf(tenantId), null, proration, sub.getCurrency(),
                "Prorrateo upgrade a " + newPlan.getName(), Map.of()
            ));
            if (!result.success()) {
                throw new RuntimeException("Pago de prorrateo fallido: " + result.errorMessage());
            }
        }

        sub.setPlanId(newPlanId);
        sub.setAmount(newPlan.getPriceMonthly());
        subscriptionRepository.save(sub);

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        tenant.setPlanId(newPlanId);
        if (newPlan.getMaxContacts() != null) tenant.setMaxClients(newPlan.getMaxContacts());
        if (newPlan.getMaxStorageMb() != null) tenant.setMaxStorageMb(newPlan.getMaxStorageMb());
        tenantRepository.save(tenant);

        log.info("Plan changed for tenant {}: {} -> {} (proration: {})", tenantId, oldPlan.getName(), newPlan.getName(), proration);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", isUpgrade ? "UPGRADE" : "DOWNGRADE");
        result.put("oldPlan", oldPlan.getName());
        result.put("newPlan", newPlan.getName());
        result.put("prorationAmount", proration);
        result.put("effectiveDate", LocalDateTime.now().toString());
        return result;
    }

    public BigDecimal calculateProration(Subscription sub, Plan oldPlan, Plan newPlan) {
        if (sub.getCurrentPeriodStart() == null || sub.getCurrentPeriodEnd() == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime now = LocalDateTime.now();
        long totalDays = ChronoUnit.DAYS.between(sub.getCurrentPeriodStart(), sub.getCurrentPeriodEnd());
        long remainingDays = ChronoUnit.DAYS.between(now, sub.getCurrentPeriodEnd());

        if (remainingDays <= 0) return BigDecimal.ZERO;

        BigDecimal oldDailyRate = oldPlan.getPriceMonthly().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal newDailyRate = newPlan.getPriceMonthly().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);

        BigDecimal oldRemaining = oldDailyRate.multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal newRemaining = newDailyRate.multiply(BigDecimal.valueOf(remainingDays));

        return newRemaining.subtract(oldRemaining).max(BigDecimal.ZERO);
    }

    public Map<String, Object> pauseSubscription(Long tenantId) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin suscripcion"));
        if (sub.getStatus() != Subscription.SubscriptionStatus.ACTIVE) {
            throw new RuntimeException("Solo se puede pausar una suscripcion activa");
        }
        sub.setStatus(Subscription.SubscriptionStatus.SUSPENDED);
        subscriptionRepository.save(sub);

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
        tenant.setSuspendedAt(LocalDateTime.now());
        tenant.setSuspendedReason("Paused by user");
        tenantRepository.save(tenant);

        log.info("Subscription paused for tenant {}", tenantId);
        return Map.of("status", "PAUSED", "tenantId", tenantId);
    }

    public Map<String, Object> resumeSubscription(Long tenantId) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin suscripcion"));
        if (sub.getStatus() != Subscription.SubscriptionStatus.SUSPENDED) {
            throw new RuntimeException("Solo se puede reactivar una suscripcion pausada");
        }
        sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        sub.setCurrentPeriodStart(LocalDateTime.now());
        sub.setCurrentPeriodEnd(LocalDateTime.now().plusDays(30));
        subscriptionRepository.save(sub);

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenant.setSuspendedAt(null);
        tenant.setSuspendedReason(null);
        tenantRepository.save(tenant);

        log.info("Subscription resumed for tenant {}", tenantId);
        return Map.of("status", "ACTIVE", "tenantId", tenantId);
    }

    public Map<String, Object> cancelSubscription(Long tenantId, String reason) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin suscripcion"));
        sub.setStatus(Subscription.SubscriptionStatus.CANCELLED);
        sub.setAutoRenew(false);
        sub.setCancelledAt(LocalDateTime.now());
        sub.setCancelReason(reason);
        subscriptionRepository.save(sub);

        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        tenant.setStatus(Tenant.TenantStatus.CANCELLED);
        tenantRepository.save(tenant);

        log.info("Subscription cancelled for tenant {}: {}", tenantId, reason);
        return Map.of("status", "CANCELLED", "tenantId", tenantId, "reason", reason);
    }

    public Map<String, Object> refundPayment(Long invoiceId, BigDecimal amount, String providerName) {
        BillingInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        PaymentProvider provider = providerFactory.getProvider(providerName);
        PaymentResult result = provider.refund(invoice.getTransactionId(), amount);

        if (result.success()) {
            invoice.setStatus(BillingInvoice.InvoiceStatus.REFUNDED);
            invoiceRepository.save(invoice);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", result.success());
        response.put("refundTransactionId", result.transactionId());
        response.put("invoiceId", invoiceId);
        response.put("amount", amount);
        return response;
    }

    public Map<String, Object> retryPayment(Long invoiceId, Long paymentMethodId, String providerName) {
        BillingInvoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (invoice.getStatus() == BillingInvoice.InvoiceStatus.PAID) {
            throw new RuntimeException("La factura ya esta pagada");
        }

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new RuntimeException("Metodo de pago no encontrado"));

        PaymentProvider provider = providerFactory.getProvider(providerName);
        PaymentResult result = provider.charge(new PaymentRequest(
            String.valueOf(invoice.getTenantId()),
            pm.getToken(),
            invoice.getAmount(),
            invoice.getCurrency(),
            "Pago factura " + invoice.getNumber(),
            Map.of("invoiceId", String.valueOf(invoiceId))
        ));

        if (result.success()) {
            invoice.setStatus(BillingInvoice.InvoiceStatus.PAID);
            invoice.setTransactionId(result.transactionId());
            invoice.setPaidDate(LocalDateTime.now());
            invoiceRepository.save(invoice);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", result.success());
        response.put("transactionId", result.transactionId());
        response.put("error", result.errorMessage());
        return response;
    }

    public Map<String, Object> getLifecycleStatus(Long tenantId) {
        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst().orElse(null);
        if (sub == null) return Map.of("error", "Sin suscripcion");

        Plan plan = planRepository.findById(sub.getPlanId()).orElse(null);
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("subscriptionStatus", sub.getStatus());
        status.put("planName", plan != null ? plan.getName() : "Unknown");
        status.put("billingCycle", sub.getBillingCycle());
        status.put("autoRenew", sub.getAutoRenew());
        status.put("currentPeriodStart", sub.getCurrentPeriodStart());
        status.put("currentPeriodEnd", sub.getCurrentPeriodEnd());
        status.put("amount", sub.getAmount());
        status.put("currency", sub.getCurrency());
        status.put("cancelledAt", sub.getCancelledAt());
        return status;
    }
}
