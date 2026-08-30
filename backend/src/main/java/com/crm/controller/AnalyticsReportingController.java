/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.AnalyticsReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsReportingController {

    private final AnalyticsReportingService service;

    // === Dashboards (Item 45) ===
    @GetMapping("/dashboards")
    public ResponseEntity<List<AnalyticsDashboard>> getDashboards() { return ResponseEntity.ok(service.getDashboards()); }

    @PostMapping("/dashboards")
    public ResponseEntity<AnalyticsDashboard> createDashboard(@RequestBody AnalyticsDashboard dashboard) { return ResponseEntity.ok(service.createDashboard(dashboard)); }

    @PutMapping("/dashboards/{id}")
    public ResponseEntity<AnalyticsDashboard> updateDashboard(@PathVariable Long id, @RequestBody AnalyticsDashboard dashboard) { return ResponseEntity.ok(service.updateDashboard(id, dashboard)); }

    // === Reports (Item 46) ===
    @GetMapping("/reports")
    public ResponseEntity<List<ReportDefinition>> getReports() { return ResponseEntity.ok(service.getReports()); }

    @PostMapping("/reports")
    public ResponseEntity<ReportDefinition> createReport(@RequestBody ReportDefinition report) { return ResponseEntity.ok(service.createReport(report)); }

    @PostMapping("/reports/{id}/execute")
    public ResponseEntity<ReportExecution> executeReport(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.executeReport(id, body.get("parameters"), body.get("format")));
    }

    @GetMapping("/reports/{id}/executions")
    public ResponseEntity<List<ReportExecution>> getExecutions(@PathVariable Long id) { return ResponseEntity.ok(service.getReportExecutions(id)); }

    // === KPIs (Item 47) ===
    @GetMapping("/kpis")
    public ResponseEntity<List<KpiDefinition>> getKPIs() { return ResponseEntity.ok(service.getKPIs()); }

    @PostMapping("/kpis")
    public ResponseEntity<KpiDefinition> createKPI(@RequestBody KpiDefinition kpi) { return ResponseEntity.ok(service.createKPI(kpi)); }

    @PostMapping("/kpis/{id}/snapshot")
    public ResponseEntity<KpiSnapshot> captureSnapshot(@PathVariable Long id) { return ResponseEntity.ok(service.captureKpiSnapshot(id)); }

    @GetMapping("/kpis/{id}/snapshots")
    public ResponseEntity<List<KpiSnapshot>> getSnapshots(@PathVariable Long id) { return ResponseEntity.ok(service.getKpiSnapshots(id)); }

    // === Data Export (Item 48) ===
    @GetMapping("/exports")
    public ResponseEntity<List<DataExport>> getExports() { return ResponseEntity.ok(service.getExports()); }

    @PostMapping("/exports")
    public ResponseEntity<DataExport> requestExport(@RequestBody DataExport export) { return ResponseEntity.ok(service.requestExport(export)); }

    // === Usage Analytics (Item 49) ===
    @GetMapping("/usage")
    public ResponseEntity<List<UsageRecord>> getUsage() { return ResponseEntity.ok(service.getUsageRecords()); }

    @GetMapping("/usage/summary")
    public ResponseEntity<Map<String, Object>> getUsageSummary() { return ResponseEntity.ok(service.getUsageSummary()); }

    // === AI Predictions (Item 50) ===
    @GetMapping("/predictions")
    public ResponseEntity<List<AIPrediction>> getPredictions() { return ResponseEntity.ok(service.getPredictions()); }

    @GetMapping("/predictions/summary")
    public ResponseEntity<Map<String, Object>> getPredictionSummary() { return ResponseEntity.ok(service.getPredictionSummary()); }

    // === Executive Dashboard (Item 51) ===
    @GetMapping("/executive")
    public ResponseEntity<Map<String, Object>> getExecutiveDashboard() { return ResponseEntity.ok(service.getExecutiveDashboard()); }

    // === Sales Analytics (Item 52) ===
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics() { return ResponseEntity.ok(service.getSalesAnalytics()); }

    // === Customer Analytics (Item 53) ===
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics() { return ResponseEntity.ok(service.getCustomerAnalytics()); }

    // === Real-time Metrics (Item 54) ===
    @GetMapping("/realtime")
    public ResponseEntity<Map<String, Object>> getRealtimeMetrics() { return ResponseEntity.ok(service.getRealtimeMetrics()); }
}
