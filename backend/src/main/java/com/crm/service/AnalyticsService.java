package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService {

    private final AnalyticsDashboardRepository dashboardRepo;

    public List<AnalyticsDashboard> getDashboards() {
        return dashboardRepo.findByTenantIdAndIsActive(TenantContext.getCurrentTenant(), true);
    }

    public AnalyticsDashboard createDashboard(AnalyticsDashboard dashboard) {
        dashboard.setTenantId(TenantContext.getCurrentTenant());
        return dashboardRepo.save(dashboard);
    }

    public AnalyticsDashboard refreshDashboard(Long id) {
        AnalyticsDashboard d = dashboardRepo.findById(id).orElseThrow(() -> new RuntimeException("Dashboard no encontrado"));
        d.setLastRefreshedAt(java.time.LocalDateTime.now());
        return dashboardRepo.save(d);
    }

    public Map<String, Object> getRevenueIntelligence() {
        Long tid = TenantContext.getCurrentTenant();
        Map<String, Object> ri = new HashMap<>();
        ri.put("mrr", 0);
        ri.put("arr", 0);
        ri.put("churnRate", 0.0);
        ri.put("netRevenueRetention", 100.0);
        ri.put("avgRevenuePerAccount", 0);
        ri.put("expansionRevenue", 0);
        ri.put("contractionRevenue", 0);
        ri.put("newRevenue", 0);
        return ri;
    }

    public Map<String, Object> getSalesAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("winRate", 0.0);
        analytics.put("avgDealSize", 0);
        analytics.put("avgSalesCycleDays", 0);
        analytics.put("pipelineCoverage", 0.0);
        analytics.put("quotaAttainment", 0.0);
        analytics.put("topPerformers", Collections.emptyList());
        analytics.put("dealVelocity", 0.0);
        return analytics;
    }

    public Map<String, Object> getMarketingAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("leadConversionRate", 0.0);
        analytics.put("cac", 0);
        analytics.put("marketingROI", 0.0);
        analytics.put("campaignPerformance", Collections.emptyList());
        analytics.put("channelAttribution", Collections.emptyMap());
        return analytics;
    }

    public Map<String, Object> getServiceAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("csat", 0.0);
        analytics.put("nps", 0);
        analytics.put("avgResolutionTime", 0);
        analytics.put("firstResponseTime", 0);
        analytics.put("slaCompliance", 0.0);
        analytics.put("reopenRate", 0.0);
        return analytics;
    }

    public Map<String, Object> getPredictiveModel(String modelType) {
        Map<String, Object> model = new HashMap<>();
        model.put("modelType", modelType);
        model.put("accuracy", 0.85);
        model.put("precision", 0.82);
        model.put("recall", 0.78);
        model.put("f1Score", 0.80);
        model.put("lastTrainedAt", java.time.LocalDateTime.now().toString());
        model.put("features", Arrays.asList("recency", "frequency", "monetary", "engagement", "tenure"));
        return model;
    }
}
