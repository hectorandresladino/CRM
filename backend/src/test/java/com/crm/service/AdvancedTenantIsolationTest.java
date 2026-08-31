package com.crm.service;

import com.crm.entity.AnalyticsDashboard;
import com.crm.entity.Commission;
import com.crm.entity.KnowledgeArticle;
import com.crm.entity.SalesForecast;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AdvancedTenantIsolationTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void forecastCannotBeApprovedAcrossTenants() {
        SalesForecastRepository forecasts = mock(SalesForecastRepository.class);
        SalesCloudService service = salesService(forecasts, mock(CommissionRepository.class));
        TenantContext.setCurrentTenant(17L);
        when(forecasts.findByTenantIdAndId(17L, 91L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.approveForecast(91L));

        verify(forecasts, never()).findById(91L);
        verify(forecasts, never()).save(any());
    }

    @Test
    void commissionCannotBePaidAcrossTenants() {
        CommissionRepository commissions = mock(CommissionRepository.class);
        SalesCloudService service = salesService(mock(SalesForecastRepository.class), commissions);
        TenantContext.setCurrentTenant(17L);
        when(commissions.findByTenantIdAndId(17L, 31L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.payCommission(31L));

        verify(commissions, never()).findById(31L);
        verify(commissions, never()).save(any());
    }

    @Test
    void articleCannotBePublishedAcrossTenants() {
        KnowledgeArticleRepository articles = mock(KnowledgeArticleRepository.class);
        ServiceCloudService service = serviceCloud(articles);
        TenantContext.setCurrentTenant(23L);
        when(articles.findByTenantIdAndId(23L, 8L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.publishArticle(8L));

        verify(articles, never()).findById(8L);
        verify(articles, never()).save(any());
    }

    @Test
    void articleVoteHandlesLegacyNullCounters() {
        KnowledgeArticleRepository articles = mock(KnowledgeArticleRepository.class);
        ServiceCloudService service = serviceCloud(articles);
        TenantContext.setCurrentTenant(23L);
        KnowledgeArticle article = new KnowledgeArticle();
        article.setViewCount(null);
        article.setHelpfulCount(null);
        when(articles.findByTenantIdAndId(23L, 8L)).thenReturn(Optional.of(article));
        when(articles.save(article)).thenReturn(article);

        KnowledgeArticle saved = service.voteArticle(8L, true);

        assertEquals(1, saved.getViewCount());
        assertEquals(1, saved.getHelpfulCount());
    }

    @Test
    void dashboardCannotBeRefreshedAcrossTenants() {
        AnalyticsDashboardRepository dashboards = mock(AnalyticsDashboardRepository.class);
        AnalyticsService service = new AnalyticsService(dashboards);
        TenantContext.setCurrentTenant(29L);
        when(dashboards.findByTenantIdAndId(29L, 6L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.refreshDashboard(6L));

        verify(dashboards, never()).findById(6L);
        verify(dashboards, never()).save(any(AnalyticsDashboard.class));
    }

    @Test
    void advancedServicesFailClosedWithoutAuthenticatedTenant() {
        SalesCloudService service = salesService(mock(SalesForecastRepository.class), mock(CommissionRepository.class));
        SalesForecast untrusted = new SalesForecast();
        untrusted.setTenantId(999L);

        assertThrows(IllegalStateException.class, () -> service.createForecast(untrusted));
    }

    private SalesCloudService salesService(SalesForecastRepository forecasts, CommissionRepository commissions) {
        return new SalesCloudService(
                forecasts,
                mock(TerritoryRepository.class),
                mock(AccountTeamRepository.class),
                mock(OpportunitySplitRepository.class),
                commissions,
                mock(SalesSequenceRepository.class)
        );
    }

    private ServiceCloudService serviceCloud(KnowledgeArticleRepository articles) {
        return new ServiceCloudService(
                articles,
                mock(EntitlementRepository.class),
                mock(ServiceMilestoneRepository.class),
                mock(FieldServiceOrderRepository.class)
        );
    }
}
