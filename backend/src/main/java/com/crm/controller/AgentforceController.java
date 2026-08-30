/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.AgentforceService;
import com.crm.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agentforce")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgentforceController {

    private final AgentforceService agentService;
    private final AnalyticsService analyticsService;

    @PostMapping("/predict/opportunity/{id}")
    public ResponseEntity<AIPrediction> predictOpportunity(@PathVariable Long id) { return ResponseEntity.ok(agentService.predictOpportunityScore(id)); }

    @PostMapping("/predict/churn/{clientId}")
    public ResponseEntity<AIPrediction> predictChurn(@PathVariable Long clientId) { return ResponseEntity.ok(agentService.predictChurn(clientId)); }

    @PostMapping("/predict/next-best-action/{clientId}")
    public ResponseEntity<AIPrediction> nextBestAction(@PathVariable Long clientId) { return ResponseEntity.ok(agentService.nextBestAction(clientId)); }

    @PostMapping("/predict/forecast")
    public ResponseEntity<AIPrediction> forecastIA(@RequestParam Integer year, @RequestParam Integer quarter) { return ResponseEntity.ok(agentService.forecastIA(year, quarter)); }

    @PostMapping("/predict/ltv/{clientId}")
    public ResponseEntity<AIPrediction> lifetimeValue(@PathVariable Long clientId) { return ResponseEntity.ok(agentService.lifetimeValue(clientId)); }

    @GetMapping("/predictions")
    public ResponseEntity<List<AIPrediction>> getPredictions(@RequestParam(required = false) String type) { return ResponseEntity.ok(agentService.getPredictions(type)); }

    @PutMapping("/predictions/{id}/action")
    public ResponseEntity<AIPrediction> actionPrediction(@PathVariable Long id) { return ResponseEntity.ok(agentService.actionPrediction(id)); }

    @GetMapping("/insights")
    public ResponseEntity<Map<String, Object>> getInsights() { return ResponseEntity.ok(agentService.getAIInsights()); }

    @GetMapping("/analytics/revenue-intelligence")
    public ResponseEntity<Map<String, Object>> getRevenueIntelligence() { return ResponseEntity.ok(analyticsService.getRevenueIntelligence()); }

    @GetMapping("/analytics/sales")
    public ResponseEntity<Map<String, Object>> getSalesAnalytics() { return ResponseEntity.ok(analyticsService.getSalesAnalytics()); }

    @GetMapping("/analytics/marketing")
    public ResponseEntity<Map<String, Object>> getMarketingAnalytics() { return ResponseEntity.ok(analyticsService.getMarketingAnalytics()); }

    @GetMapping("/analytics/service")
    public ResponseEntity<Map<String, Object>> getServiceAnalytics() { return ResponseEntity.ok(analyticsService.getServiceAnalytics()); }

    @GetMapping("/analytics/predictive/{modelType}")
    public ResponseEntity<Map<String, Object>> getPredictiveModel(@PathVariable String modelType) { return ResponseEntity.ok(analyticsService.getPredictiveModel(modelType)); }

    @GetMapping("/dashboards")
    public ResponseEntity<List<AnalyticsDashboard>> getDashboards() { return ResponseEntity.ok(analyticsService.getDashboards()); }

    @PostMapping("/dashboards")
    public ResponseEntity<AnalyticsDashboard> createDashboard(@RequestBody AnalyticsDashboard d) { return ResponseEntity.ok(analyticsService.createDashboard(d)); }

    @PutMapping("/dashboards/{id}/refresh")
    public ResponseEntity<AnalyticsDashboard> refreshDashboard(@PathVariable Long id) { return ResponseEntity.ok(analyticsService.refreshDashboard(id)); }
}
