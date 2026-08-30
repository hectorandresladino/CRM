/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.PlatformInfrastructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
@RequiredArgsConstructor
public class PlatformInfrastructureController {

    private final PlatformInfrastructureService service;

    // === Feature Flags (Item 65) ===
    @GetMapping("/feature-flags")
    public ResponseEntity<List<FeatureFlag>> getFeatureFlags() { return ResponseEntity.ok(service.getFeatureFlags()); }

    @PostMapping("/feature-flags")
    public ResponseEntity<FeatureFlag> createFeatureFlag(@RequestBody FeatureFlag flag) { return ResponseEntity.ok(service.createFeatureFlag(flag)); }

    @PostMapping("/feature-flags/{id}/toggle")
    public ResponseEntity<FeatureFlag> toggleFeatureFlag(@PathVariable Long id) { return ResponseEntity.ok(service.toggleFeatureFlag(id)); }

    @GetMapping("/feature-flags/check")
    public ResponseEntity<Map<String, Boolean>> checkFeature(@RequestParam String key) {
        return ResponseEntity.ok(Map.of("enabled", service.isFeatureEnabled(key)));
    }

    // === System Settings (Item 66) ===
    @GetMapping("/settings")
    public ResponseEntity<List<SystemSetting>> getSettings() { return ResponseEntity.ok(service.getSettings()); }

    @GetMapping("/settings/category/{category}")
    public ResponseEntity<List<SystemSetting>> getSettingsByCategory(@PathVariable String category) { return ResponseEntity.ok(service.getSettingsByCategory(category)); }

    @PostMapping("/settings")
    public ResponseEntity<SystemSetting> saveSetting(@RequestBody SystemSetting setting) { return ResponseEntity.ok(service.createOrUpdateSetting(setting)); }

    @GetMapping("/settings/value")
    public ResponseEntity<Map<String, String>> getSettingValue(@RequestParam String key, @RequestParam(required = false, defaultValue = "") String defaultValue) {
        return ResponseEntity.ok(Map.of("value", service.getSettingValue(key, defaultValue)));
    }

    // === Rate Limiting (Item 67) ===
    @GetMapping("/rate-limits")
    public ResponseEntity<List<RateLimitConfig>> getRateLimits() { return ResponseEntity.ok(service.getRateLimits()); }

    @PostMapping("/rate-limits")
    public ResponseEntity<RateLimitConfig> createRateLimit(@RequestBody RateLimitConfig config) { return ResponseEntity.ok(service.createRateLimit(config)); }

    @GetMapping("/rate-limits/check")
    public ResponseEntity<Map<String, Boolean>> checkRateLimit(@RequestParam String endpoint) {
        return ResponseEntity.ok(Map.of("allowed", service.checkRateLimit(endpoint)));
    }

    @PostMapping("/rate-limits/reset")
    public ResponseEntity<Void> resetRateCounters() { service.resetRateCounters(); return ResponseEntity.ok().build(); }

    // === File Storage (Item 68) ===
    @GetMapping("/files")
    public ResponseEntity<List<FileStorageRecord>> getFiles() { return ResponseEntity.ok(service.getFiles()); }

    @PostMapping("/files")
    public ResponseEntity<FileStorageRecord> recordUpload(@RequestBody FileStorageRecord record) { return ResponseEntity.ok(service.recordFileUpload(record)); }

    @PostMapping("/files/{id}/download")
    public ResponseEntity<FileStorageRecord> trackDownload(@PathVariable Long id) { return ResponseEntity.ok(service.incrementDownloadCount(id)); }

    @GetMapping("/files/stats")
    public ResponseEntity<Map<String, Object>> getStorageStats() { return ResponseEntity.ok(service.getStorageStats()); }

    // === Backups (Item 69) ===
    @GetMapping("/backups")
    public ResponseEntity<List<BackupRecord>> getBackups() { return ResponseEntity.ok(service.getBackups()); }

    @PostMapping("/backups")
    public ResponseEntity<BackupRecord> createBackup(@RequestBody BackupRecord backup) { return ResponseEntity.ok(service.createBackup(backup)); }

    // === API Keys (Item 70) ===
    @GetMapping("/api-keys")
    public ResponseEntity<List<ApiKey>> getApiKeys() { return ResponseEntity.ok(service.getApiKeys()); }

    @PostMapping("/api-keys")
    public ResponseEntity<ApiKey> createApiKey(@RequestBody ApiKey apiKey) { return ResponseEntity.ok(service.createApiKey(apiKey)); }

    @PostMapping("/api-keys/{id}/revoke")
    public ResponseEntity<ApiKey> revokeApiKey(@PathVariable Long id) { return ResponseEntity.ok(service.revokeApiKey(id)); }

    // === Audit Logs (Item 71) ===
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() { return ResponseEntity.ok(service.getAuditLogs()); }

    @PostMapping("/audit-logs")
    public ResponseEntity<AuditLog> logAction(@RequestBody AuditLog log) { return ResponseEntity.ok(service.logAction(log)); }

    @GetMapping("/audit-logs/stats")
    public ResponseEntity<Map<String, Object>> getAuditStats() { return ResponseEntity.ok(service.getAuditStats()); }

    // === Notifications (Item 72) ===
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications() { return ResponseEntity.ok(service.getNotifications()); }

    @PostMapping("/notifications")
    public ResponseEntity<Notification> sendNotification(@RequestBody Notification notification) { return ResponseEntity.ok(service.sendNotification(notification)); }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Notification> markRead(@PathVariable Long id) { return ResponseEntity.ok(service.markNotificationRead(id)); }

    // === Currencies (Item 73) ===
    @GetMapping("/currencies")
    public ResponseEntity<List<com.crm.entity.Currency>> getCurrencies() { return ResponseEntity.ok(service.getCurrencies()); }

    @PostMapping("/currencies")
    public ResponseEntity<com.crm.entity.Currency> createCurrency(@RequestBody com.crm.entity.Currency currency) { return ResponseEntity.ok(service.createCurrency(currency)); }

    @GetMapping("/currencies/rates")
    public ResponseEntity<List<CurrencyRate>> getExchangeRates() { return ResponseEntity.ok(service.getExchangeRates()); }

    @PostMapping("/currencies/rates")
    public ResponseEntity<CurrencyRate> updateRate(@RequestBody CurrencyRate rate) { return ResponseEntity.ok(service.updateExchangeRate(rate)); }

    // === Platform Dashboard (Item 74) ===
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() { return ResponseEntity.ok(service.getPlatformDashboard()); }
}
