package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BillingInvoiceRepository billingInvoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    private static final DateTimeFormatter INVOICE_NUMBER_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public BillingInvoice generateInvoice(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado"));

        Subscription sub = subscriptionRepository.findByTenantId(tenantId).stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Sin suscripción activa"));

        Plan plan = planRepository.findById(sub.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        BigDecimal amount = sub.getBillingCycle() == Subscription.BillingCycle.YEARLY
                ? plan.getPriceYearly() : plan.getPriceMonthly();

        BillingInvoice invoice = new BillingInvoice();
        invoice.setTenantId(tenantId);
        invoice.setSubscriptionId(sub.getId());
        invoice.setNumber("INV-" + LocalDateTime.now().format(INVOICE_NUMBER_FMT) + "-" + tenantId + "-" + System.currentTimeMillis() % 10000);
        invoice.setPlanId(plan.getId());
        invoice.setAmount(amount);
        invoice.setCurrency(plan.getCurrency());
        invoice.setStatus(BillingInvoice.InvoiceStatus.PENDING);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setDueDate(LocalDateTime.now().plusDays(7));
        invoice.setBillingPeriodStart(sub.getCurrentPeriodStart());
        invoice.setBillingPeriodEnd(sub.getCurrentPeriodEnd());

        return billingInvoiceRepository.save(invoice);
    }

    @Transactional
    public Payment processPayment(Long invoiceId, Long paymentMethodId, String provider) {
        BillingInvoice invoice = billingInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        Payment payment = new Payment();
        payment.setTenantId(invoice.getTenantId());
        payment.setBillingInvoiceId(invoice.getId());
        payment.setAmount(invoice.getAmount());
        payment.setCurrency(invoice.getCurrency());
        payment.setGateway(provider.toUpperCase());
        payment.setGatewayTransactionId("PENDING-" + System.currentTimeMillis());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        return payment;
    }

    @Transactional
    public void markInvoicePaid(Long invoiceId, String transactionId) {
        BillingInvoice invoice = billingInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        invoice.setStatus(BillingInvoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        billingInvoiceRepository.save(invoice);

        Subscription sub = subscriptionRepository.findById(invoice.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
        sub.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        sub.setCurrentPeriodStart(LocalDateTime.now());
        sub.setCurrentPeriodEnd(sub.getBillingCycle() == Subscription.BillingCycle.YEARLY
                ? LocalDateTime.now().plusYears(1) : LocalDateTime.now().plusMonths(1));
        subscriptionRepository.save(sub);

        Tenant tenant = tenantRepository.findById(invoice.getTenantId()).orElse(null);
        if (tenant != null) {
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
            tenantRepository.save(tenant);
        }

        log.info("Factura {} pagada, tenant {} activado", invoice.getNumber(), invoice.getTenantId());
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void checkOverdueInvoices() {
        List<BillingInvoice> pending = billingInvoiceRepository.findByStatus(BillingInvoice.InvoiceStatus.PENDING);
        AtomicInteger count = new AtomicInteger(0);
        for (BillingInvoice inv : pending) {
            if (inv.getDueDate() != null && inv.getDueDate().isBefore(LocalDateTime.now())) {
                inv.setStatus(BillingInvoice.InvoiceStatus.OVERDUE);
                billingInvoiceRepository.save(inv);
                count.incrementAndGet();
            }
        }
        if (count.get() > 0) {
            log.info("Marcadas {} facturas como vencidas", count.get());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void checkExpiredTrials() {
        List<Tenant> trials = tenantRepository.findByStatus(Tenant.TenantStatus.TRIAL);
        for (Tenant tenant : trials) {
            if (tenant.getTrialEndsAt() != null && tenant.getTrialEndsAt().isBefore(LocalDateTime.now())) {
                tenant.setStatus(Tenant.TenantStatus.EXPIRED);
                tenantRepository.save(tenant);

                Subscription sub = subscriptionRepository.findByTenantId(tenant.getId()).stream().findFirst().orElse(null);
                if (sub != null) {
                    sub.setStatus(Subscription.SubscriptionStatus.EXPIRED);
                    subscriptionRepository.save(sub);
                }
                log.info("Trial expirado para tenant {}", tenant.getName());
            }
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void generateRecurringInvoices() {
        List<Subscription> activeSubs = subscriptionRepository.findByStatus(Subscription.SubscriptionStatus.ACTIVE);
        for (Subscription sub : activeSubs) {
            if (sub.getCurrentPeriodEnd() != null && sub.getCurrentPeriodEnd().isBefore(LocalDateTime.now().plusDays(1))) {
                generateInvoice(sub.getTenantId());
                log.info("Factura recurrente generada para tenant {}", sub.getTenantId());
            }
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    @Transactional
    public void suspendOverdueTenants() {
        List<Tenant> active = tenantRepository.findByStatus(Tenant.TenantStatus.ACTIVE);
        for (Tenant tenant : active) {
            List<BillingInvoice> overdue = billingInvoiceRepository.findByTenantIdAndStatus(
                    tenant.getId(), BillingInvoice.InvoiceStatus.OVERDUE);
            if (!overdue.isEmpty()) {
                boolean hasOldOverdue = overdue.stream()
                        .anyMatch(inv -> inv.getDueDate().isBefore(LocalDateTime.now().minusDays(7)));
                if (hasOldOverdue) {
                    tenant.setStatus(Tenant.TenantStatus.SUSPENDED);
                    tenant.setSuspendedReason("Pago vencido por más de 7 días");
                    tenantRepository.save(tenant);

                    Subscription sub = subscriptionRepository.findByTenantId(tenant.getId()).stream().findFirst().orElse(null);
                    if (sub != null) {
                        sub.setStatus(Subscription.SubscriptionStatus.SUSPENDED);
                        subscriptionRepository.save(sub);
                    }
                    log.warn("Tenant {} suspendido por impago", tenant.getName());
                }
            }
        }
    }
}
