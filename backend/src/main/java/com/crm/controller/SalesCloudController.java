/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.SalesCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales-cloud")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalesCloudController {

    private final SalesCloudService service;

    @GetMapping("/territories")
    public ResponseEntity<List<Territory>> getTerritories() { return ResponseEntity.ok(service.getTerritories()); }

    @PostMapping("/territories")
    public ResponseEntity<Territory> createTerritory(@RequestBody Territory t) { return ResponseEntity.ok(service.createTerritory(t)); }

    @GetMapping("/account-teams/{accountId}")
    public ResponseEntity<List<AccountTeam>> getAccountTeam(@PathVariable Long accountId) { return ResponseEntity.ok(service.getAccountTeam(accountId)); }

    @PostMapping("/account-teams")
    public ResponseEntity<AccountTeam> addMember(@RequestBody AccountTeam member) { return ResponseEntity.ok(service.addAccountTeamMember(member)); }

    @GetMapping("/opportunity-splits/{oppId}")
    public ResponseEntity<List<OpportunitySplit>> getSplits(@PathVariable Long oppId) { return ResponseEntity.ok(service.getSplits(oppId)); }

    @PostMapping("/opportunity-splits/{oppId}")
    public ResponseEntity<List<OpportunitySplit>> createSplits(@PathVariable Long oppId, @RequestBody List<OpportunitySplit> splits) { return ResponseEntity.ok(service.createSplits(oppId, splits)); }

    @GetMapping("/forecasts")
    public ResponseEntity<List<SalesForecast>> getForecasts(@RequestParam(required = false) Integer year) { return ResponseEntity.ok(service.getForecasts(year)); }

    @PostMapping("/forecasts")
    public ResponseEntity<SalesForecast> createForecast(@RequestBody SalesForecast f) { return ResponseEntity.ok(service.createForecast(f)); }

    @PutMapping("/forecasts/{id}/submit")
    public ResponseEntity<SalesForecast> submitForecast(@PathVariable Long id) { return ResponseEntity.ok(service.submitForecast(id)); }

    @PutMapping("/forecasts/{id}/approve")
    public ResponseEntity<SalesForecast> approveForecast(@PathVariable Long id) { return ResponseEntity.ok(service.approveForecast(id)); }

    @GetMapping("/forecasts/summary")
    public ResponseEntity<Map<String, Object>> getForecastSummary(@RequestParam Integer year) { return ResponseEntity.ok(service.getForecastSummary(year)); }

    @GetMapping("/commissions")
    public ResponseEntity<List<Commission>> getCommissions(@RequestParam Long userId, @RequestParam Integer year, @RequestParam Integer month) { return ResponseEntity.ok(service.calculateCommissions(userId, year, month)); }

    @PostMapping("/commissions")
    public ResponseEntity<Commission> createCommission(@RequestBody Commission c) { return ResponseEntity.ok(service.createCommission(c)); }

    @PutMapping("/commissions/{id}/approve")
    public ResponseEntity<Commission> approveCommission(@PathVariable Long id) { return ResponseEntity.ok(service.approveCommission(id)); }

    @PutMapping("/commissions/{id}/pay")
    public ResponseEntity<Commission> payCommission(@PathVariable Long id) { return ResponseEntity.ok(service.payCommission(id)); }

    @GetMapping("/sequences")
    public ResponseEntity<List<SalesSequence>> getSequences() { return ResponseEntity.ok(service.getSequences()); }

    @PostMapping("/sequences")
    public ResponseEntity<SalesSequence> createSequence(@RequestBody SalesSequence s) { return ResponseEntity.ok(service.createSequence(s)); }
}
