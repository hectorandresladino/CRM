package com.crm.service;

import com.crm.entity.Actividad;
import com.crm.repository.ActividadRepository;
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

class ActividadServiceTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void completingAnActivityFailsClosedOutsideTheTenant() {
        ActividadRepository repository = mock(ActividadRepository.class);
        ActividadService service = new ActividadService(repository);
        TenantContext.setCurrentTenant(4L);
        when(repository.findByTenantIdAndId(4L, 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.complete(99L));

        verify(repository, never()).findById(99L);
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void newActivityAlwaysReceivesAuthenticatedTenant() {
        ActividadRepository repository = mock(ActividadRepository.class);
        ActividadService service = new ActividadService(repository);
        TenantContext.setCurrentTenant(4L);
        Actividad activity = new Actividad();
        activity.setTenantId(999L);
        when(repository.save(activity)).thenReturn(activity);

        Actividad saved = service.save(activity);

        assertEquals(4L, saved.getTenantId());
    }
}
