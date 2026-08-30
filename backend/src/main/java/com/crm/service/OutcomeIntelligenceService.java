/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Cliente;
import com.crm.entity.Prospecto;
import com.crm.entity.ServicioCliente;
import com.crm.entity.Tenant;
import com.crm.entity.Venta;
import com.crm.repository.ClienteRepository;
import com.crm.repository.ProspectoRepository;
import com.crm.repository.ServicioClienteRepository;
import com.crm.repository.TenantRepository;
import com.crm.repository.VentaRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Proprietary product differentiator: translates CRM activity into one
 * explainable business outcome score and a prioritized action list.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutcomeIntelligenceService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProspectoRepository prospectoRepository;
    private final ServicioClienteRepository serviceRepository;
    private final TenantRepository tenantRepository;

    public Map<String, Object> scorecard() {
        Long tenantId = TenantContext.requireCurrentTenant();
        List<Venta> sales = ventaRepository.findByTenantId(tenantId);
        List<Cliente> clients = clienteRepository.findByTenantId(tenantId);
        List<ServicioCliente> cases = serviceRepository.findByTenantId(tenantId);
        List<Prospecto> prospects = prospectoRepository.findByTenantId(tenantId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant autenticado no encontrado"));

        long won = sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.CERRADA).count();
        long lost = sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.CANCELADA).count();
        long open = sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.PENDIENTE
                || v.getEstado() == Venta.EstadoVenta.EN_PROCESO).count();
        BigDecimal revenue = sum(sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.CERRADA).toList());
        BigDecimal openPipeline = sum(sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.PENDIENTE
                || v.getEstado() == Venta.EstadoVenta.EN_PROCESO).toList());

        double winRate = ratio(won, won + lost) * 100;
        long convertedProspects = prospects.stream().filter(p -> p.getClienteId() != null
                || p.getEstado() == Prospecto.EstadoProspecto.CERRADO).count();
        double leadConversion = ratio(convertedProspects, prospects.size()) * 100;
        double activeClientRate = ratio(clients.stream()
                .filter(c -> c.getEstado() == Cliente.EstadoCliente.ACTIVO).count(), clients.size()) * 100;
        double resolutionRate = ratio(cases.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.RESUELTO
                || c.getEstado() == ServicioCliente.EstadoServicio.CERRADO).count(), cases.size()) * 100;
        double pipelineCoverage = revenue.signum() == 0
                ? (openPipeline.signum() > 0 ? 3 : 0)
                : openPipeline.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue();

        double outcomeScore = round(clamp(winRate, 0, 100) * .30
                + clamp(pipelineCoverage / 3 * 100, 0, 100) * .25
                + activeClientRate * .20
                + resolutionRate * .15
                + clamp(leadConversion * 2, 0, 100) * .10);

        List<Map<String, String>> actions = new ArrayList<>();
        if (pipelineCoverage < 3) actions.add(action("HIGH", "BUILD_PIPELINE", "Elevar cobertura de pipeline a 3x del ingreso cerrado."));
        if (winRate < 25) actions.add(action("HIGH", "IMPROVE_WIN_RATE", "Revisar motivos de pérdida y calificación de oportunidades."));
        if (resolutionRate < 80) actions.add(action("MEDIUM", "REDUCE_SERVICE_BACKLOG", "Priorizar casos abiertos y vencimientos de SLA."));
        if (leadConversion < 10) actions.add(action("MEDIUM", "IMPROVE_LEAD_CONVERSION", "Ajustar scoring, cadencias y velocidad de contacto."));
        if (actions.isEmpty()) actions.add(action("LOW", "SCALE_WHAT_WORKS", "Mantener controles y escalar los segmentos de mejor rendimiento."));

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("revenue", revenue);
        metrics.put("openPipeline", openPipeline);
        metrics.put("pipelineCoverage", round(pipelineCoverage));
        metrics.put("winRate", round(winRate));
        metrics.put("leadConversionRate", round(leadConversion));
        metrics.put("activeClientRate", round(activeClientRate));
        metrics.put("serviceResolutionRate", round(resolutionRate));
        metrics.put("wonDeals", won);
        metrics.put("lostDeals", lost);
        metrics.put("openDeals", open);
        metrics.put("prospects", prospects.size());
        metrics.put("convertedProspects", convertedProspects);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("outcomeScore", outcomeScore);
        result.put("calculationType", "TENANT_REAL_DATA_EXPLAINABLE");
        result.put("formulaVersion", "outcome-score-1.1");
        result.put("period", "ALL_TIME");
        result.put("currency", tenant.getCurrency());
        result.put("metrics", metrics);
        result.put("recommendedActions", actions);
        result.put("generatedAt", LocalDateTime.now());
        return result;
    }

    private BigDecimal sum(List<Venta> sales) {
        return sales.stream().map(v -> v.getTotal() != null ? v.getTotal()
                        : v.getMonto() != null ? v.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double ratio(long numerator, long denominator) {
        return denominator == 0 ? 0 : (double) numerator / denominator;
    }

    private Map<String, String> action(String priority, String code, String message) {
        return Map.of("priority", priority, "code", code, "message", message);
    }

    private double clamp(double value, double min, double max) { return Math.max(min, Math.min(max, value)); }
    private double round(double value) { return Math.round(value * 100.0) / 100.0; }
}
