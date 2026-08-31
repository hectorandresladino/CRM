package com.crm.service;

import com.crm.entity.ABTest;
import com.crm.entity.FlowDefinition;
import com.crm.entity.Integration;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketingAutomationHardeningTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void abTestCannotBeUpdatedAcrossTenants() {
        ABTestRepository tests = mock(ABTestRepository.class);
        MarketingCloudAdvancedService service = marketingService(tests);
        TenantContext.setCurrentTenant(41L);
        when(tests.findByTenantIdAndId(41L, 7L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.recordVisit(7L, "A"));

        verify(tests, never()).findById(7L);
        verify(tests, never()).save(any());
    }

    @Test
    void abTestRejectsUnknownVariant() {
        ABTestRepository tests = mock(ABTestRepository.class);
        MarketingCloudAdvancedService service = marketingService(tests);
        TenantContext.setCurrentTenant(41L);
        ABTest test = new ABTest();
        when(tests.findByTenantIdAndId(41L, 7L)).thenReturn(Optional.of(test));

        assertThrows(IllegalArgumentException.class, () -> service.recordConversion(7L, "C"));

        verify(tests, never()).save(any());
    }

    @Test
    void abTestConfidenceCompletesOnlyForSignificantDifference() {
        ABTestRepository tests = mock(ABTestRepository.class);
        MarketingCloudAdvancedService service = marketingService(tests);
        TenantContext.setCurrentTenant(41L);
        ABTest test = new ABTest();
        test.setVariantAVisits(30);
        test.setVariantAConversions(24);
        test.setVariantBVisits(30);
        test.setVariantBConversions(2);
        test.setStatus(ABTest.TestStatus.RUNNING);
        when(tests.findByTenantIdAndId(41L, 7L)).thenReturn(Optional.of(test));
        when(tests.save(test)).thenReturn(test);

        ABTest saved = service.recordVisit(7L, "A");

        assertTrue(saved.getConfidenceLevel() >= 95.0);
        assertEquals(ABTest.TestStatus.COMPLETED, saved.getStatus());
        assertEquals("A", saved.getWinningVariant());
    }

    @Test
    void legacyAutomationRouteNeverStoresRawCredentials() {
        IntegrationRepository integrations = mock(IntegrationRepository.class);
        AutomationIntegrationService service = automationService(mock(FlowDefinitionRepository.class), integrations);
        TenantContext.setCurrentTenant(51L);
        Integration integration = new Integration();
        when(integrations.findByTenantIdAndId(51L, 9L)).thenReturn(Optional.of(integration));

        assertThrows(UnsupportedOperationException.class,
                () -> service.connectIntegration(9L, "plaintext-secret"));

        assertNull(integration.getCredentials());
        verify(integrations, never()).save(any());
    }

    @Test
    void automationConnectorCreationIsFailClosed() {
        IntegrationRepository integrations = mock(IntegrationRepository.class);
        AutomationIntegrationService service = automationService(mock(FlowDefinitionRepository.class), integrations);
        TenantContext.setCurrentTenant(51L);
        Integration integration = new Integration();
        integration.setTenantId(999L);
        integration.setCredentials("plaintext-secret");
        integration.setConnected(true);
        integration.setSyncEnabled(true);
        when(integrations.save(integration)).thenReturn(integration);

        Integration saved = service.createIntegration(integration);

        assertEquals(51L, saved.getTenantId());
        assertNull(saved.getCredentials());
        assertFalse(saved.getConnected());
        assertFalse(saved.getSyncEnabled());
    }

    @Test
    void flowCannotBeActivatedAcrossTenants() {
        FlowDefinitionRepository flows = mock(FlowDefinitionRepository.class);
        AutomationIntegrationService service = automationService(flows, mock(IntegrationRepository.class));
        TenantContext.setCurrentTenant(51L);
        when(flows.findByTenantIdAndId(51L, 11L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.activateFlow(11L));

        verify(flows, never()).findById(11L);
        verify(flows, never()).save(any(FlowDefinition.class));
    }

    private MarketingCloudAdvancedService marketingService(ABTestRepository tests) {
        return new MarketingCloudAdvancedService(
                mock(CampanaMarketingRepository.class),
                mock(EmailMarketingRepository.class),
                mock(CustomerSegmentRepository.class),
                mock(CustomerJourneyRepository.class),
                mock(JourneyStepRepository.class),
                mock(LandingPageRepository.class),
                mock(MarketingAttributionRepository.class),
                mock(SocialMediaPostRepository.class),
                tests,
                mock(ClienteRepository.class)
        );
    }

    private AutomationIntegrationService automationService(
            FlowDefinitionRepository flows, IntegrationRepository integrations) {
        return new AutomationIntegrationService(
                flows,
                mock(FlowExecutionLogRepository.class),
                mock(WorkflowAutomationRepository.class),
                mock(WebhookRepository.class),
                mock(WebhookLogRepository.class),
                integrations,
                mock(IntegrationSyncLogRepository.class),
                mock(ScheduledJobRepository.class),
                mock(ApprovalProcessRepository.class),
                mock(ValidationRuleRepository.class)
        );
    }
}
