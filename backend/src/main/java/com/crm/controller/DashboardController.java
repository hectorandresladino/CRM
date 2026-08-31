/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.repository.ClienteRepository;
import com.crm.repository.ProspectoRepository;
import com.crm.repository.VentaRepository;
import com.crm.repository.CotizacionRepository;
import com.crm.repository.PedidoRepository;
import com.crm.repository.ServicioClienteRepository;
import com.crm.entity.Cliente;
import com.crm.entity.ServicioCliente;
import com.crm.entity.Venta;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final ClienteRepository clienteRepository;
    private final ProspectoRepository prospectoRepository;
    private final VentaRepository ventaRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PedidoRepository pedidoRepository;
    private final ServicioClienteRepository servicioClienteRepository;
    
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Object> stats = new HashMap<>();

        stats.put("clientesActivos", clienteRepository.findByTenantIdAndEstado(tenantId, Cliente.EstadoCliente.ACTIVO).size());
        stats.put("prospectos", prospectoRepository.findByTenantId(tenantId).size());
        stats.put("ventas", ventaRepository.findByTenantId(tenantId).size());
        stats.put("cotizaciones", cotizacionRepository.findByTenantId(tenantId).size());
        stats.put("pedidos", pedidoRepository.findByTenantId(tenantId).size());
        stats.put("tickets", servicioClienteRepository.findByTenantId(tenantId).stream()
                .filter(ticket -> ticket.getEstado() != ServicioCliente.EstadoServicio.RESUELTO
                        && ticket.getEstado() != ServicioCliente.EstadoServicio.CERRADO)
                .count());
        
        return stats;
    }
    
    @GetMapping("/ventas-mes")
    public Map<String, Object> getVentasMes() {
        Long tenantId = TenantContext.requireCurrentTenant();
        LocalDate currentMonth = LocalDate.now();
        var sales = ventaRepository.findByTenantIdAndEstado(tenantId, Venta.EstadoVenta.CERRADA).stream()
                .filter(sale -> sale.getFechaCierre() != null
                        && sale.getFechaCierre().getYear() == currentMonth.getYear()
                        && sale.getFechaCierre().getMonthValue() == currentMonth.getMonthValue())
                .toList();
        Map<String, Object> data = new HashMap<>();
        data.put("total", sales.size());
        data.put("monto", sales.stream()
                .map(sale -> sale.getTotal() != null ? sale.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return data;
    }
}
