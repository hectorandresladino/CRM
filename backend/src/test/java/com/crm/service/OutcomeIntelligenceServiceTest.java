package com.crm.service;

import com.crm.entity.Tenant;
import com.crm.entity.Venta;
import com.crm.repository.ClienteRepository;
import com.crm.repository.ProspectoRepository;
import com.crm.repository.ServicioClienteRepository;
import com.crm.repository.TenantRepository;
import com.crm.repository.VentaRepository;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OutcomeIntelligenceServiceTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    @SuppressWarnings("unchecked")
    void comparesSelectedPeriodWithTheImmediatelyPreviousPeriod() {
        VentaRepository sales = mock(VentaRepository.class);
        ClienteRepository clients = mock(ClienteRepository.class);
        ProspectoRepository prospects = mock(ProspectoRepository.class);
        ServicioClienteRepository cases = mock(ServicioClienteRepository.class);
        TenantRepository tenants = mock(TenantRepository.class);
        OutcomeIntelligenceService service = new OutcomeIntelligenceService(sales, clients, prospects, cases, tenants);

        Tenant tenant = new Tenant();
        tenant.setId(7L);
        tenant.setCurrency("USD");
        TenantContext.setCurrentTenant(7L);

        when(tenants.findById(7L)).thenReturn(Optional.of(tenant));
        when(sales.findByTenantId(7L)).thenReturn(List.of(
                closedSale("200", LocalDateTime.now().minusDays(10)),
                closedSale("100", LocalDateTime.now().minusDays(40))));
        when(clients.findByTenantId(7L)).thenReturn(List.of());
        when(prospects.findByTenantId(7L)).thenReturn(List.of());
        when(cases.findByTenantId(7L)).thenReturn(List.of());

        Map<String, Object> result = service.scorecard(30);
        Map<String, Object> metrics = (Map<String, Object>) result.get("metrics");
        Map<String, Object> comparison = (Map<String, Object>) result.get("comparison");

        assertEquals("LAST_30_DAYS", result.get("period"));
        assertEquals(new BigDecimal("200"), metrics.get("revenue"));
        assertEquals(100.0, comparison.get("revenueChangePct"));
        assertNotNull(result.get("periodContext"));
    }

    private Venta closedSale(String total, LocalDateTime closedAt) {
        Venta sale = new Venta();
        sale.setEstado(Venta.EstadoVenta.CERRADA);
        sale.setTotal(new BigDecimal(total));
        sale.setFechaCreacion(closedAt.minusDays(2));
        sale.setFechaCierre(closedAt);
        return sale;
    }
}
