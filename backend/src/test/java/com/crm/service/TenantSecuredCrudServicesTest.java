package com.crm.service;

import com.crm.entity.CampanaMarketing;
import com.crm.entity.WhatsAppBusiness;
import com.crm.repository.CampanaMarketingRepository;
import com.crm.repository.WhatsAppBusinessRepository;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TenantSecuredCrudServicesTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void campaignCannotBeDeletedAcrossTenants() {
        CampanaMarketingRepository repository = mock(CampanaMarketingRepository.class);
        CampanaMarketingService service = new CampanaMarketingService(repository);
        TenantContext.setCurrentTenant(7L);
        when(repository.findByTenantIdAndId(7L, 41L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.delete(41L));

        verify(repository, never()).deleteById(41L);
        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void campaignAlwaysUsesAuthenticatedTenantOnCreate() {
        CampanaMarketingRepository repository = mock(CampanaMarketingRepository.class);
        CampanaMarketingService service = new CampanaMarketingService(repository);
        TenantContext.setCurrentTenant(7L);
        CampanaMarketing campaign = new CampanaMarketing();
        campaign.setTenantId(999L);
        when(repository.save(campaign)).thenReturn(campaign);

        CampanaMarketing saved = service.save(campaign);

        assertEquals(7L, saved.getTenantId());
    }

    @Test
    void whatsappLookupNeverUsesGlobalId() {
        WhatsAppBusinessRepository repository = mock(WhatsAppBusinessRepository.class);
        WhatsAppBusinessService service = new WhatsAppBusinessService(repository);
        TenantContext.setCurrentTenant(12L);
        when(repository.findByTenantIdAndId(12L, 5L)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), service.findById(5L));
        verify(repository, never()).findById(5L);
    }
}
