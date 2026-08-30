/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsReportingService {

    private final AnalyticsDashboardRepository dashboardRepo;
    private final ReportDefinitionRepository reportRepo;
    private final ReportExecutionRepository executionRepo;
    private final KpiDefinitionRepository kpiRepo;
    private final KpiSnapshotRepository kpiSnapshotRepo;
    private final DataExportRepository exportRepo;
    private final UsageRecordRepository usageRepo;
    private final AIPredictionRepository predictionRepo;
    private final VentaRepository ventaRepo;
    private final ClienteRepository clienteRepo;
    private final CotizacionRepository cotizacionRepo;
    private final PedidoRepository pedidoRepo;
    private final ServicioClienteRepository servicioRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Dashboards (Item 45) ===

    public List<AnalyticsDashboard> getDashboards() { return dashboardRepo.findByTenantId(tid()); }

    public AnalyticsDashboard createDashboard(AnalyticsDashboard dashboard) {
        dashboard.setTenantId(tid());
        return dashboardRepo.save(dashboard);
    }

    public AnalyticsDashboard updateDashboard(Long id, AnalyticsDashboard updated) {
        AnalyticsDashboard d = dashboardRepo.findById(id).orElseThrow();
        d.setName(updated.getName());
        d.setDescription(updated.getDescription());
        d.setWidgets(updated.getWidgets());
        d.setFilters(updated.getFilters());
        d.setRefreshFrequency(updated.getRefreshFrequency());
        d.setIsShared(updated.getIsShared());
        d.setSharedWith(updated.getSharedWith());
        d.setLastRefreshedAt(LocalDateTime.now());
        return dashboardRepo.save(d);
    }

    // === Report Builder (Item 46) ===

    public List<ReportDefinition> getReports() { return reportRepo.findByTenantId(tid()); }

    public ReportDefinition createReport(ReportDefinition report) {
        report.setTenantId(tid());
        return reportRepo.save(report);
    }

    public ReportExecution executeReport(Long reportId, String parameters, String format) {
        ReportDefinition report = reportRepo.findById(reportId).orElseThrow();
        ReportExecution exec = new ReportExecution();
        exec.setTenantId(tid());
        exec.setReportId(reportId);
        exec.setReportName(report.getName());
        exec.setFormat(format != null ? format : report.getFormat());
        exec.setParameters(parameters);
        exec.setStatus(ReportExecution.ExecutionStatus.RUNNING);
        exec.setStartedAt(LocalDateTime.now());
        exec = executionRepo.save(exec);

        long startTime = System.currentTimeMillis();

        try {
            List<Map<String, Object>> data = generateReportData(report, parameters);
            exec.setRowCount(data.size());
            exec.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            exec.setStatus(ReportExecution.ExecutionStatus.COMPLETED);
            exec.setCompletedAt(LocalDateTime.now());
            exec.setFileUrl("/exports/report-" + exec.getId() + "." + exec.getFormat().toLowerCase());
        } catch (Exception e) {
            exec.setStatus(ReportExecution.ExecutionStatus.FAILED);
            exec.setErrorMessage(e.getMessage());
            exec.setCompletedAt(LocalDateTime.now());
        }

        return executionRepo.save(exec);
    }

    public List<ReportExecution> getReportExecutions(Long reportId) {
        return executionRepo.findByTenantIdAndReportId(tid(), reportId);
    }

    private List<Map<String, Object>> generateReportData(ReportDefinition report, String parameters) {
        String source = report.getDataSource() != null ? report.getDataSource() : "VENTAS";
        return switch (source.toUpperCase()) {
            case "VENTAS" -> generateSalesReport();
            case "CLIENTES" -> generateClientReport();
            case "COTIZACIONES" -> generateQuoteReport();
            case "PEDIDOS" -> generateOrderReport();
            case "SERVICIO" -> generateServiceReport();
            default -> new ArrayList<>();
        };
    }

    private List<Map<String, Object>> generateSalesReport() {
        List<Venta> ventas = ventaRepo.findByTenantId(tid());
        List<Map<String, Object>> data = new ArrayList<>();
        for (Venta v : ventas) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", v.getId());
            row.put("cliente", v.getCliente() != null ? v.getCliente().getNombre() + " " + v.getCliente().getApellido() : "");
            row.put("fecha", v.getFechaCreacion());
            row.put("total", v.getTotal());
            row.put("estado", v.getEstado());
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> generateClientReport() {
        List<Cliente> clientes = clienteRepo.findByTenantId(tid());
        List<Map<String, Object>> data = new ArrayList<>();
        for (Cliente c : clientes) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", c.getId());
            row.put("nombre", c.getNombre() + " " + c.getApellido());
            row.put("email", c.getEmail());
            row.put("empresa", c.getEmpresa());
            row.put("estado", c.getEstado());
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> generateQuoteReport() {
        List<Cotizacion> cotizaciones = cotizacionRepo.findByTenantId(tid());
        List<Map<String, Object>> data = new ArrayList<>();
        for (Cotizacion c : cotizaciones) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", c.getId());
            row.put("cliente", c.getCliente() != null ? c.getCliente().getNombre() : "");
            row.put("fecha", c.getFechaCreacion());
            row.put("total", c.getTotal());
            row.put("estado", c.getEstado());
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> generateOrderReport() {
        List<Pedido> pedidos = pedidoRepo.findByTenantId(tid());
        List<Map<String, Object>> data = new ArrayList<>();
        for (Pedido p : pedidos) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", p.getId());
            row.put("cliente", p.getCliente() != null ? p.getCliente().getNombre() : "");
            row.put("fecha", p.getFechaCreacion());
            row.put("total", p.getTotal());
            row.put("estado", p.getEstado());
            data.add(row);
        }
        return data;
    }

    private List<Map<String, Object>> generateServiceReport() {
        List<ServicioCliente> casos = servicioRepo.findByTenantId(tid());
        List<Map<String, Object>> data = new ArrayList<>();
        for (ServicioCliente s : casos) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", s.getId());
            row.put("codigo", s.getCodigo());
            row.put("asunto", s.getAsunto());
            row.put("tipo", s.getTipo());
            row.put("prioridad", s.getPrioridad());
            row.put("estado", s.getEstado());
            row.put("fechaCreacion", s.getFechaCreacion());
            data.add(row);
        }
        return data;
    }

    // === KPI Management (Item 47) ===

    public List<KpiDefinition> getKPIs() { return kpiRepo.findByTenantId(tid()); }

    public KpiDefinition createKPI(KpiDefinition kpi) {
        kpi.setTenantId(tid());
        return kpiRepo.save(kpi);
    }

    public KpiSnapshot captureKpiSnapshot(Long kpiId) {
        KpiDefinition kpi = kpiRepo.findById(kpiId).orElseThrow();
        BigDecimal actualValue = calculateKpiValue(kpi);
        BigDecimal targetValue = kpi.getTargetValue() != null ? kpi.getTargetValue() : BigDecimal.ZERO;

        double attainment = targetValue.compareTo(BigDecimal.ZERO) > 0
                ? actualValue.divide(targetValue, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;

        List<KpiSnapshot> previous = kpiSnapshotRepo.findByTenantIdAndKpiId(tid(), kpiId);
        BigDecimal prevValue = previous.isEmpty() ? BigDecimal.ZERO : previous.get(previous.size() - 1).getActualValue();
        double changePct = prevValue.compareTo(BigDecimal.ZERO) > 0
                ? actualValue.subtract(prevValue).divide(prevValue, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;

        KpiSnapshot snapshot = new KpiSnapshot();
        snapshot.setTenantId(tid());
        snapshot.setKpiId(kpiId);
        snapshot.setActualValue(actualValue);
        snapshot.setTargetValue(targetValue);
        snapshot.setAttainmentPercentage(attainment);
        snapshot.setPreviousValue(prevValue);
        snapshot.setChangePercentage(changePct);
        snapshot.setTrend(changePct > 0 ? "UP" : changePct < 0 ? "DOWN" : "FLAT");

        if (attainment >= 100) snapshot.setStatus("EXCEEDED");
        else if (attainment >= 80) snapshot.setStatus("ON_TRACK");
        else if (attainment >= 60) snapshot.setStatus("WARNING");
        else snapshot.setStatus("AT_RISK");

        LocalDateTime now = LocalDateTime.now();
        snapshot.setPeriodStart(now.minusDays(30));
        snapshot.setPeriodEnd(now);

        return kpiSnapshotRepo.save(snapshot);
    }

    public List<KpiSnapshot> getKpiSnapshots(Long kpiId) {
        return kpiSnapshotRepo.findByTenantIdAndKpiId(tid(), kpiId);
    }

    private BigDecimal calculateKpiValue(KpiDefinition kpi) {
        String metric = kpi.getMetricName() != null ? kpi.getMetricName().toUpperCase() : "REVENUE";
        return switch (metric) {
            case "REVENUE" -> ventaRepo.findByTenantId(tid()).stream()
                    .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            case "CLIENT_COUNT" -> BigDecimal.valueOf(clienteRepo.findByTenantId(tid()).size());
            case "QUOTE_COUNT" -> BigDecimal.valueOf(cotizacionRepo.findByTenantId(tid()).size());
            case "ORDER_COUNT" -> BigDecimal.valueOf(pedidoRepo.findByTenantId(tid()).size());
            case "CASE_COUNT" -> BigDecimal.valueOf(servicioRepo.findByTenantId(tid()).size());
            default -> BigDecimal.ZERO;
        };
    }

    // === Data Export (Item 48) ===

    public List<DataExport> getExports() { return exportRepo.findByTenantId(tid()); }

    public DataExport requestExport(DataExport export) {
        export.setTenantId(tid());
        export.setStatus(DataExport.ExportStatus.PENDING);
        export.setStartedAt(LocalDateTime.now());
        export = exportRepo.save(export);

        try {
            int recordCount = performExport(export);
            export.setTotalRecords(recordCount);
            export.setStatus(DataExport.ExportStatus.COMPLETED);
            export.setCompletedAt(LocalDateTime.now());
            export.setExpiresAt(LocalDateTime.now().plusDays(7));
            export.setFileUrl("/exports/data-" + export.getId() + "." + export.getExportFormat().toLowerCase());
        } catch (Exception e) {
            export.setStatus(DataExport.ExportStatus.FAILED);
            export.setErrorMessage(e.getMessage());
        }

        return exportRepo.save(export);
    }

    private int performExport(DataExport export) {
        String entity = export.getEntityType() != null ? export.getEntityType().toUpperCase() : "CLIENTES";
        return switch (entity) {
            case "CLIENTES" -> clienteRepo.findByTenantId(tid()).size();
            case "VENTAS" -> ventaRepo.findByTenantId(tid()).size();
            case "COTIZACIONES" -> cotizacionRepo.findByTenantId(tid()).size();
            case "PEDIDOS" -> pedidoRepo.findByTenantId(tid()).size();
            case "SERVICIOS" -> servicioRepo.findByTenantId(tid()).size();
            default -> 0;
        };
    }

    // === Usage Analytics (Item 49) ===

    public List<UsageRecord> getUsageRecords() { return usageRepo.findByTenantId(tid()); }

    public Map<String, Object> getUsageSummary() {
        List<UsageRecord> records = usageRepo.findByTenantId(tid());
        Map<String, Object> summary = new LinkedHashMap<>();

        Map<String, BigDecimal> byMetric = new LinkedHashMap<>();
        for (UsageRecord r : records) {
            String metric = r.getMetricName() != null ? r.getMetricName() : "UNKNOWN";
            byMetric.merge(metric, r.getMetricValue(), BigDecimal::add);
        }
        summary.put("usageByMetric", byMetric);

        long unbilled = records.stream().filter(r -> !Boolean.TRUE.equals(r.getIsBilled())).count();
        summary.put("unbilledRecords", unbilled);
        summary.put("totalRecords", records.size());

        return summary;
    }

    // === AI Predictions Analytics (Item 50) ===

    public List<AIPrediction> getPredictions() { return predictionRepo.findByTenantId(tid()); }

    public Map<String, Object> getPredictionSummary() {
        List<AIPrediction> predictions = predictionRepo.findByTenantId(tid());
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalPredictions", predictions.size());

        Map<String, Long> byType = new LinkedHashMap<>();
        for (AIPrediction p : predictions) {
            String type = p.getPredictionType() != null ? p.getPredictionType() : "UNKNOWN";
            byType.merge(type, 1L, Long::sum);
        }
        summary.put("byType", byType);

        long actioned = predictions.stream().filter(p -> Boolean.TRUE.equals(p.getIsActioned())).count();
        summary.put("actioned", actioned);
        summary.put("actionRate", predictions.size() > 0 ? (double) actioned / predictions.size() * 100 : 0);

        double avgConfidence = predictions.stream()
                .filter(p -> p.getConfidenceScore() != null)
                .mapToDouble(AIPrediction::getConfidenceScore)
                .average().orElse(0);
        summary.put("averageConfidence", Math.round(avgConfidence * 100.0) / 100.0);

        return summary;
    }

    // === Executive Dashboard (Item 51) ===

    public Map<String, Object> getExecutiveDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        Long t = tid();

        List<Venta> ventas = ventaRepo.findByTenantId(t);
        BigDecimal totalRevenue = ventas.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.put("totalRevenue", totalRevenue);
        dashboard.put("totalSales", ventas.size());

        List<Cliente> clientes = clienteRepo.findByTenantId(t);
        dashboard.put("totalClients", clientes.size());
        dashboard.put("activeClients", clientes.stream().filter(c -> c.getEstado() == Cliente.EstadoCliente.ACTIVO).count());

        List<Cotizacion> cotizaciones = cotizacionRepo.findByTenantId(t);
        dashboard.put("totalQuotes", cotizaciones.size());

        List<Pedido> pedidos = pedidoRepo.findByTenantId(t);
        dashboard.put("totalOrders", pedidos.size());

        List<ServicioCliente> casos = servicioRepo.findByTenantId(t);
        dashboard.put("totalCases", casos.size());
        dashboard.put("openCases", casos.stream().filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.ABIERTO).count());

        dashboard.put("dashboards", dashboardRepo.findByTenantId(t).size());
        dashboard.put("reports", reportRepo.findByTenantId(t).size());
        dashboard.put("activeKPIs", kpiRepo.findByTenantIdAndIsActive(t, true).size());

        return dashboard;
    }

    // === Sales Analytics (Item 52) ===

    public Map<String, Object> getSalesAnalytics() {
        List<Venta> ventas = ventaRepo.findByTenantId(tid());
        Map<String, Object> analytics = new LinkedHashMap<>();

        BigDecimal totalRevenue = ventas.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("totalRevenue", totalRevenue);

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Venta v : ventas) {
            String status = v.getEstado() != null ? v.getEstado().name() : "UNKNOWN";
            byStatus.merge(status, 1L, Long::sum);
        }
        analytics.put("salesByStatus", byStatus);

        Map<String, BigDecimal> revenueByMonth = new LinkedHashMap<>();
        for (Venta v : ventas) {
            if (v.getFechaCreacion() != null) {
                String monthKey = v.getFechaCreacion().getYear() + "-" + String.format("%02d", v.getFechaCreacion().getMonthValue());
                BigDecimal amount = v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO;
                revenueByMonth.merge(monthKey, amount, BigDecimal::add);
            }
        }
        analytics.put("revenueByMonth", revenueByMonth);

        analytics.put("avgDealSize", ventas.size() > 0
                ? totalRevenue.divide(BigDecimal.valueOf(ventas.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return analytics;
    }

    // === Customer Analytics (Item 53) ===

    public Map<String, Object> getCustomerAnalytics() {
        List<Cliente> clientes = clienteRepo.findByTenantId(tid());
        Map<String, Object> analytics = new LinkedHashMap<>();

        analytics.put("totalClients", clientes.size());

        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Cliente.EstadoCliente status : Cliente.EstadoCliente.values()) {
            byStatus.put(status.name(), clientes.stream().filter(c -> c.getEstado() == status).count());
        }
        analytics.put("clientsByStatus", byStatus);

        Map<String, Long> bySector = new LinkedHashMap<>();
        for (Cliente c : clientes) {
            String sector = c.getSector() != null ? c.getSector() : "SIN_SECTOR";
            bySector.merge(sector, 1L, Long::sum);
        }
        analytics.put("clientsBySector", bySector);

        Map<String, Long> byCountry = new LinkedHashMap<>();
        for (Cliente c : clientes) {
            String country = c.getPais() != null ? c.getPais() : "SIN_PAIS";
            byCountry.merge(country, 1L, Long::sum);
        }
        analytics.put("clientsByCountry", byCountry);

        return analytics;
    }

    // === Real-time Metrics (Item 54) ===

    public Map<String, Object> getRealtimeMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        Long t = tid();

        metrics.put("timestamp", LocalDateTime.now().toString());
        metrics.put("activeClients", clienteRepo.findByTenantId(t).stream()
                .filter(c -> c.getEstado() == Cliente.EstadoCliente.ACTIVO).count());
        metrics.put("openCases", servicioRepo.findByTenantId(t).stream()
                .filter(c -> c.getEstado() == ServicioCliente.EstadoServicio.ABIERTO).count());
        metrics.put("pendingQuotes", cotizacionRepo.findByTenantId(t).stream()
                .filter(c -> c.getEstado() != null && "BORRADOR".equalsIgnoreCase(c.getEstado().name())).count());
        metrics.put("pendingOrders", pedidoRepo.findByTenantId(t).stream()
                .filter(p -> p.getEstado() != null && "PENDIENTE".equalsIgnoreCase(p.getEstado().name())).count());

        List<Venta> todaySales = ventaRepo.findByTenantId(t).stream()
                .filter(v -> v.getFechaCreacion() != null && v.getFechaCreacion().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .toList();
        metrics.put("todaySalesCount", todaySales.size());
        metrics.put("todayRevenue", todaySales.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return metrics;
    }
}
