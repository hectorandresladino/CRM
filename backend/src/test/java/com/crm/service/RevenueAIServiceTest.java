/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.AIPrediction;
import com.crm.entity.Venta;
import com.crm.repository.AIPredictionRepository;
import com.crm.repository.ClienteRepository;
import com.crm.repository.VentaRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RevenueAIServiceTest {

    private AIPredictionRepository predictionRepository;
    private VentaRepository ventaRepository;
    private RevenueAIService service;

    @BeforeEach
    void setup() {
        predictionRepository = mock(AIPredictionRepository.class);
        ventaRepository = mock(VentaRepository.class);
        service = new RevenueAIService(predictionRepository, mock(ClienteRepository.class), ventaRepository);
        when(predictionRepository.save(any(AIPrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TenantContext.setCurrentTenant(77L);
    }

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void opportunityScoreUsesOwnedRealDataAndIsDeterministic() {
        Venta opportunity = new Venta();
        opportunity.setId(9L);
        opportunity.setTenantId(77L);
        opportunity.setEstado(Venta.EstadoVenta.EN_PROCESO);
        opportunity.setMonto(BigDecimal.valueOf(1_000));
        opportunity.setTotal(BigDecimal.valueOf(1_000));
        opportunity.setFechaCreacion(LocalDateTime.now().minusDays(12));
        when(ventaRepository.findByIdAndTenantId(9L, 77L)).thenReturn(Optional.of(opportunity));
        when(ventaRepository.findByTenantId(77L)).thenReturn(List.of(opportunity));

        AIPrediction first = service.predictOpportunityScore(9L);
        AIPrediction second = service.predictOpportunityScore(9L);

        assertEquals(first.getPredictedValue(), second.getPredictedValue());
        assertEquals("revenue-opportunity-baseline", first.getModelName());
        assertEquals("baseline-1.0", first.getModelVersion());
        assertEquals(77L, first.getTenantId());
        assertTrue(first.getProbability() >= 0 && first.getProbability() <= 1);
        verify(ventaRepository, times(2)).findByIdAndTenantId(9L, 77L);
    }

    @Test
    void opportunityFromAnotherTenantIsNeverScored() {
        when(ventaRepository.findByIdAndTenantId(9L, 77L)).thenReturn(Optional.empty());

        assertThrows(TenantAccessDeniedException.class, () -> service.predictOpportunityScore(9L));
        verify(predictionRepository, never()).save(any());
    }
}
