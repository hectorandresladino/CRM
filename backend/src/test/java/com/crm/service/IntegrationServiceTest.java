package com.crm.service;

import com.crm.entity.Integration;
import com.crm.repository.IntegrationRepository;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class IntegrationServiceTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void catalogEntryIsNotReportedAsConnectedWithoutAProviderHandshake() {
        IntegrationRepository repository = mock(IntegrationRepository.class);
        IntegrationService service = new IntegrationService(repository);
        TenantContext.setCurrentTenant(3L);
        Integration integration = new Integration();
        integration.setTenantId(999L);
        integration.setProvider("META_BUSINESS");
        integration.setCategory("COMMUNICATION");
        integration.setConnected(true);
        when(repository.findByTenantIdAndProvider(3L, "META_BUSINESS")).thenReturn(Optional.empty());
        when(repository.save(any(Integration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Integration saved = service.connect(integration);

        assertEquals(3L, saved.getTenantId());
        assertFalse(saved.getConnected());
    }

    @Test
    void unimplementedSyncFailsHonestlyInsteadOfReturningFakeSuccess() {
        IntegrationRepository repository = mock(IntegrationRepository.class);
        IntegrationService service = new IntegrationService(repository);
        TenantContext.setCurrentTenant(3L);
        Integration integration = new Integration();
        integration.setId(9L);
        integration.setTenantId(3L);
        integration.setProvider("META_BUSINESS");
        when(repository.findByTenantIdAndId(3L, 9L)).thenReturn(Optional.of(integration));

        assertThrows(UnsupportedOperationException.class, () -> service.syncNow(9L));
    }

    @Test
    void rejectsProviderCategoryMismatch() {
        IntegrationService service = new IntegrationService(mock(IntegrationRepository.class));
        TenantContext.setCurrentTenant(3L);
        Integration integration = new Integration();
        integration.setProvider("ZAPIER");
        integration.setCategory("COMMUNICATION");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.connect(integration));

        assertTrue(error.getMessage().contains("AUTOMATION"));
    }

    @Test
    void rejectsSecretsInsidePublicConnectorConfig() {
        IntegrationService service = new IntegrationService(mock(IntegrationRepository.class));
        TenantContext.setCurrentTenant(3L);
        Integration integration = new Integration();
        integration.setProvider("META_BUSINESS");
        integration.setCategory("COMMUNICATION");
        integration.setConfig("{\"access_token\":\"should-never-be-here\"}");

        assertThrows(IllegalArgumentException.class, () -> service.connect(integration));
    }
}
