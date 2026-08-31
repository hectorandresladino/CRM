/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final VentaRepository ventaRepository;
    private final ProspectoRepository prospectoRepository;
    private final ClienteRepository clienteRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PedidoRepository pedidoRepository;

    @GetMapping("/sales-summary")
    public ResponseEntity<Map<String, Object>> getSalesSummary() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Object> summary = new HashMap<>();
        Double totalClosed = ventaRepository.sumTotalVentasCerradasByTenantId(tenantId);
        summary.put("totalClosed", totalClosed != null ? totalClosed : 0);
        summary.put("totalVentas", ventaRepository.findByTenantId(tenantId).size());
        summary.put("totalClientes", clienteRepository.countByTenantId(tenantId));
        summary.put("totalProspectos", prospectoRepository.findByTenantId(tenantId).size());
        summary.put("totalCotizaciones", cotizacionRepository.findByTenantId(tenantId).size());
        summary.put("totalPedidos", pedidoRepository.findByTenantId(tenantId).size());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/sales-by-status")
    public ResponseEntity<Map<String, Long>> getSalesByStatus() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("PENDIENTE", (long) ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.PENDIENTE).size());
        result.put("EN_PROCESO", (long) ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.EN_PROCESO).size());
        result.put("CERRADA", (long) ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.CERRADA).size());
        result.put("CANCELADA", (long) ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.CANCELADA).size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/prospectos-by-stage")
    public ResponseEntity<Map<String, Long>> getProspectosByStage() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Long> result = new LinkedHashMap<>();
        for (com.crm.entity.Prospecto.EstadoProspecto stage : com.crm.entity.Prospecto.EstadoProspecto.values()) {
            result.put(stage.name(), (long) prospectoRepository.findByTenantIdAndEstado(tenantId, stage).size());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/forecasting")
    public ResponseEntity<Map<String, Object>> getForecasting() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Object> forecast = new HashMap<>();

        List<com.crm.entity.Venta> closed = ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.CERRADA);
        List<com.crm.entity.Venta> pending = ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.PENDIENTE);
        List<com.crm.entity.Venta> inProcess = ventaRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Venta.EstadoVenta.EN_PROCESO);

        double closedValue = closed.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal().doubleValue() : 0).sum();
        double pendingValue = pending.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal().doubleValue() : 0).sum();
        double inProcessValue = inProcess.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal().doubleValue() : 0).sum();

        double weightedForecast = pendingValue * 0.3 + inProcessValue * 0.5 + closedValue;
        double bestCase = pendingValue + inProcessValue + closedValue;
        double worstCase = inProcessValue * 0.3 + closedValue;

        forecast.put("closedValue", closedValue);
        forecast.put("pendingValue", pendingValue);
        forecast.put("inProcessValue", inProcessValue);
        forecast.put("weightedForecast", weightedForecast);
        forecast.put("bestCase", bestCase);
        forecast.put("worstCase", worstCase);
        forecast.put("generatedAt", LocalDateTime.now().toString());

        return ResponseEntity.ok(forecast);
    }

    @GetMapping("/conversion-funnel")
    public ResponseEntity<Map<String, Object>> getConversionFunnel() {
        Long tenantId = TenantContext.requireCurrentTenant();
        Map<String, Object> funnel = new LinkedHashMap<>();
        long nuevos = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.NUEVO).size();
        long contactados = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.CONTACTADO).size();
        long calificados = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.CALIFICADO).size();
        long propuesta = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.PROPUESTA).size();
        long negociacion = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.NEGOCIACION).size();
        long cerrados = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.CERRADO).size();
        long perdidos = prospectoRepository.findByTenantIdAndEstado(tenantId, com.crm.entity.Prospecto.EstadoProspecto.PERDIDO).size();

        long total = nuevos + contactados + calificados + propuesta + negociacion + cerrados + perdidos;
        funnel.put("nuevos", nuevos);
        funnel.put("contactados", contactados);
        funnel.put("calificados", calificados);
        funnel.put("propuesta", propuesta);
        funnel.put("negociacion", negociacion);
        funnel.put("cerrados", cerrados);
        funnel.put("perdidos", perdidos);
        funnel.put("total", total);
        funnel.put("conversionRate", total > 0 ? (cerrados * 100.0 / total) : 0);

        return ResponseEntity.ok(funnel);
    }
}
