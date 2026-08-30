package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalesCloudService {

    private final SalesForecastRepository forecastRepo;
    private final TerritoryRepository territoryRepo;
    private final AccountTeamRepository accountTeamRepo;
    private final OpportunitySplitRepository splitRepo;
    private final CommissionRepository commissionRepo;
    private final SalesSequenceRepository sequenceRepo;

    public List<Territory> getTerritories() {
        Long tid = TenantContext.getCurrentTenant();
        return territoryRepo.findByTenantId(tid);
    }

    public Territory createTerritory(Territory t) {
        t.setTenantId(TenantContext.getCurrentTenant());
        return territoryRepo.save(t);
    }

    public List<AccountTeam> getAccountTeam(Long accountId) {
        return accountTeamRepo.findByTenantIdAndAccountId(TenantContext.getCurrentTenant(), accountId);
    }

    public AccountTeam addAccountTeamMember(AccountTeam member) {
        member.setTenantId(TenantContext.getCurrentTenant());
        return accountTeamRepo.save(member);
    }

    public List<OpportunitySplit> getSplits(Long opportunityId) {
        return splitRepo.findByTenantIdAndOpportunityId(TenantContext.getCurrentTenant(), opportunityId);
    }

    public List<OpportunitySplit> createSplits(Long opportunityId, List<OpportunitySplit> splits) {
        Long tid = TenantContext.getCurrentTenant();
        splits.forEach(s -> { s.setTenantId(tid); s.setOpportunityId(opportunityId); });
        return splitRepo.saveAll(splits);
    }

    public SalesForecast createForecast(SalesForecast forecast) {
        forecast.setTenantId(TenantContext.getCurrentTenant());
        forecast.setStatus("DRAFT");
        return forecastRepo.save(forecast);
    }

    public SalesForecast submitForecast(Long id) {
        SalesForecast f = forecastRepo.findById(id).orElseThrow(() -> new RuntimeException("Forecast no encontrado"));
        f.setStatus("SUBMITTED");
        return forecastRepo.save(f);
    }

    public SalesForecast approveForecast(Long id) {
        SalesForecast f = forecastRepo.findById(id).orElseThrow(() -> new RuntimeException("Forecast no encontrado"));
        f.setStatus("APPROVED");
        return forecastRepo.save(f);
    }

    public List<SalesForecast> getForecasts(Integer year) {
        Long tid = TenantContext.getCurrentTenant();
        if (year != null) return forecastRepo.findByTenantIdAndPeriodYear(tid, year);
        return forecastRepo.findByTenantId(tid);
    }

    public List<Commission> calculateCommissions(Long userId, Integer year, Integer month) {
        Long tid = TenantContext.getCurrentTenant();
        return commissionRepo.findByTenantIdAndPeriodYearAndPeriodMonth(tid, year, month);
    }

    public Commission createCommission(Commission c) {
        c.setTenantId(TenantContext.getCurrentTenant());
        c.setStatus("CALCULATED");
        return commissionRepo.save(c);
    }

    public Commission approveCommission(Long id) {
        Commission c = commissionRepo.findById(id).orElseThrow(() -> new RuntimeException("Comision no encontrada"));
        c.setStatus("APPROVED");
        return commissionRepo.save(c);
    }

    public Commission payCommission(Long id) {
        Commission c = commissionRepo.findById(id).orElseThrow(() -> new RuntimeException("Comision no encontrada"));
        c.setStatus("PAID");
        c.setPaidDate(LocalDateTime.now());
        return commissionRepo.save(c);
    }

    public List<SalesSequence> getSequences() {
        return sequenceRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public SalesSequence createSequence(SalesSequence s) {
        s.setTenantId(TenantContext.getCurrentTenant());
        return sequenceRepo.save(s);
    }

    public Map<String, Object> getForecastSummary(Integer year) {
        Long tid = TenantContext.getCurrentTenant();
        List<SalesForecast> forecasts = forecastRepo.findByTenantIdAndPeriodYear(tid, year);
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCommit", forecasts.stream().map(SalesForecast::getCommitAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("totalBestCase", forecasts.stream().map(SalesForecast::getBestCaseAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("totalClosed", forecasts.stream().map(SalesForecast::getClosedAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("totalPipeline", forecasts.stream().map(SalesForecast::getPipelineAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("forecastCount", forecasts.size());
        return summary;
    }
}
