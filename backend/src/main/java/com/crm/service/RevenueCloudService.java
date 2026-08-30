package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
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
public class RevenueCloudService {

    private final SubscriptionRepository subscriptionRepo;
    private final SubscriptionAmendmentRepository amendmentRepo;
    private final UsageRecordRepository usageRepo;
    private final DunningCampaignRepository dunningRepo;
    private final PlanRepository planRepo;

    public SubscriptionAmendment createAmendment(SubscriptionAmendment amendment) {
        amendment.setTenantId(TenantContext.getCurrentTenant());
        amendment.setStatus("PENDING");

        Subscription sub = subscriptionRepo.findById(amendment.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Suscripcion no encontrada"));

        if (amendment.getAmendmentType().contains("UPGRADE") || amendment.getAmendmentType().contains("DOWNGRADE")) {
            Plan oldPlan = planRepo.findById(amendment.getOldPlanId()).orElse(null);
            Plan newPlan = planRepo.findById(amendment.getNewPlanId()).orElse(null);
            if (oldPlan != null && newPlan != null) {
                BigDecimal oldAmount = sub.getBillingCycle() == Subscription.BillingCycle.YEARLY
                        ? oldPlan.getPriceYearly() : oldPlan.getPriceMonthly();
                BigDecimal newAmount = sub.getBillingCycle() == Subscription.BillingCycle.YEARLY
                        ? newPlan.getPriceYearly() : newPlan.getPriceMonthly();
                amendment.setOldAmount(oldAmount);
                amendment.setNewAmount(newAmount);
                amendment.setProrationAmount(calculateProration(oldAmount, newAmount, sub.getCurrentPeriodStart(), sub.getCurrentPeriodEnd()));
            }
        }

        return amendmentRepo.save(amendment);
    }

    public SubscriptionAmendment processAmendment(Long id) {
        SubscriptionAmendment a = amendmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Enmienda no encontrada"));
        a.setStatus("PROCESSED");
        a.setProcessedAt(LocalDateTime.now());

        if (a.getNewPlanId() != null) {
            Subscription sub = subscriptionRepo.findById(a.getSubscriptionId()).orElse(null);
            if (sub != null) {
                sub.setPlanId(a.getNewPlanId());
                subscriptionRepo.save(sub);
            }
        }

        return amendmentRepo.save(a);
    }

    public BigDecimal calculateProration(BigDecimal oldAmount, BigDecimal newAmount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (periodStart == null || periodEnd == null) return BigDecimal.ZERO;
        long totalDays = ChronoUnit.DAYS.between(periodStart, periodEnd);
        long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), periodEnd);
        if (totalDays <= 0 || remainingDays <= 0) return BigDecimal.ZERO;

        BigDecimal dailyOld = oldAmount.divide(BigDecimal.valueOf(totalDays), 4, RoundingMode.HALF_UP);
        BigDecimal dailyNew = newAmount.divide(BigDecimal.valueOf(totalDays), 4, RoundingMode.HALF_UP);
        BigDecimal oldRemaining = dailyOld.multiply(BigDecimal.valueOf(remainingDays));
        BigDecimal newRemaining = dailyNew.multiply(BigDecimal.valueOf(remainingDays));
        return newRemaining.subtract(oldRemaining).setScale(2, RoundingMode.HALF_UP);
    }

    public UsageRecord recordUsage(UsageRecord record) {
        record.setTenantId(TenantContext.getCurrentTenant());
        return usageRepo.save(record);
    }

    public List<UsageRecord> getUsageRecords(Long subscriptionId) {
        Long tid = TenantContext.getCurrentTenant();
        if (subscriptionId != null) return usageRepo.findByTenantIdAndSubscriptionId(tid, subscriptionId);
        return usageRepo.findByTenantId(tid);
    }

    public List<UsageRecord> getUnbilledUsage() {
        return usageRepo.findByTenantIdAndIsBilled(TenantContext.getCurrentTenant(), false);
    }

    public DunningCampaign createDunningStep(DunningCampaign dunning) {
        dunning.setTenantId(TenantContext.getCurrentTenant());
        return dunningRepo.save(dunning);
    }

    public List<DunningCampaign> getDunningCampaigns(Long invoiceId) {
        Long tid = TenantContext.getCurrentTenant();
        if (invoiceId != null) return dunningRepo.findByTenantIdAndInvoiceId(tid, invoiceId);
        return dunningRepo.findByTenantId(tid);
    }

    public DunningCampaign sendDunningStep(Long id) {
        DunningCampaign d = dunningRepo.findById(id).orElseThrow(() -> new RuntimeException("Dunning no encontrado"));
        d.setStatus("SENT");
        d.setSentAt(LocalDateTime.now());
        return dunningRepo.save(d);
    }

    public Map<String, Object> getSubscriptionLifecycle(Long subscriptionId) {
        Subscription sub = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Suscripcion no encontrada"));
        Long tid = TenantContext.getCurrentTenant();
        Map<String, Object> lifecycle = new HashMap<>();
        lifecycle.put("subscription", sub);
        lifecycle.put("amendments", amendmentRepo.findByTenantIdAndSubscriptionId(tid, subscriptionId));
        lifecycle.put("usageRecords", usageRepo.findByTenantIdAndSubscriptionId(tid, subscriptionId));
        lifecycle.put("plan", planRepo.findById(sub.getPlanId()).orElse(null));
        return lifecycle;
    }
}
