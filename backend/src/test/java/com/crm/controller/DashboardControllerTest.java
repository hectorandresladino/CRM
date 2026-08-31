package com.crm.controller;

import com.crm.entity.Cliente;
import com.crm.entity.ServicioCliente;
import com.crm.repository.ClienteRepository;
import com.crm.repository.CotizacionRepository;
import com.crm.repository.PedidoRepository;
import com.crm.repository.ProspectoRepository;
import com.crm.repository.ServicioClienteRepository;
import com.crm.repository.VentaRepository;
import com.crm.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardControllerTest {

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    @Test
    void statsUseOnlyTenantScopedRepositoryMethods() {
        ClienteRepository clients = mock(ClienteRepository.class);
        ProspectoRepository prospects = mock(ProspectoRepository.class);
        VentaRepository sales = mock(VentaRepository.class);
        CotizacionRepository quotes = mock(CotizacionRepository.class);
        PedidoRepository orders = mock(PedidoRepository.class);
        ServicioClienteRepository cases = mock(ServicioClienteRepository.class);
        DashboardController controller = new DashboardController(clients, prospects, sales, quotes, orders, cases);
        TenantContext.setCurrentTenant(21L);

        ServicioCliente openCase = new ServicioCliente();
        openCase.setEstado(ServicioCliente.EstadoServicio.ABIERTO);
        when(clients.findByTenantIdAndEstado(21L, Cliente.EstadoCliente.ACTIVO)).thenReturn(List.of(new Cliente()));
        when(prospects.findByTenantId(21L)).thenReturn(List.of());
        when(sales.findByTenantId(21L)).thenReturn(List.of());
        when(quotes.findByTenantId(21L)).thenReturn(List.of());
        when(orders.findByTenantId(21L)).thenReturn(List.of());
        when(cases.findByTenantId(21L)).thenReturn(List.of(openCase));

        Map<String, Object> stats = controller.getStats();

        assertEquals(1, stats.get("clientesActivos"));
        assertEquals(1L, stats.get("tickets"));
        verify(clients, never()).count();
        verify(prospects, never()).count();
        verify(sales, never()).count();
    }
}
