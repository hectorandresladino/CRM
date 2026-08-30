/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.MobileOfflineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile")
@RequiredArgsConstructor
public class MobileOfflineController {

    private final MobileOfflineService service;

    // === Device Management (Item 85) ===
    @GetMapping("/devices")
    public ResponseEntity<List<MobileDevice>> getDevices() { return ResponseEntity.ok(service.getDevices()); }

    @GetMapping("/devices/user/{userId}")
    public ResponseEntity<List<MobileDevice>> getUserDevices(@PathVariable Long userId) { return ResponseEntity.ok(service.getUserDevices(userId)); }

    @PostMapping("/devices/register")
    public ResponseEntity<MobileDevice> registerDevice(@RequestBody MobileDevice device) { return ResponseEntity.ok(service.registerDevice(device)); }

    @PostMapping("/devices/{id}/unregister")
    public ResponseEntity<MobileDevice> unregisterDevice(@PathVariable Long id) { return ResponseEntity.ok(service.unregisterDevice(id)); }

    @PostMapping("/devices/{id}/update-token")
    public ResponseEntity<MobileDevice> updateToken(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.updateDeviceLocation(id, body.get("pushToken")));
    }

    // === Offline Sync (Item 86) ===
    @GetMapping("/sync/queue")
    public ResponseEntity<List<OfflineSyncQueue>> getSyncQueue() { return ResponseEntity.ok(service.getSyncQueue()); }

    @GetMapping("/sync/pending")
    public ResponseEntity<List<OfflineSyncQueue>> getPendingSync() { return ResponseEntity.ok(service.getPendingSyncItems()); }

    @PostMapping("/sync/queue")
    public ResponseEntity<OfflineSyncQueue> queueOperation(@RequestBody OfflineSyncQueue item) { return ResponseEntity.ok(service.queueOfflineOperation(item)); }

    @PostMapping("/sync/{id}/process")
    public ResponseEntity<OfflineSyncQueue> processSyncItem(@PathVariable Long id) { return ResponseEntity.ok(service.processSyncItem(id)); }

    @GetMapping("/sync/stats")
    public ResponseEntity<Map<String, Object>> getSyncStats() { return ResponseEntity.ok(service.getSyncStats()); }

    // === Push Notifications (Item 87) ===
    @GetMapping("/push/logs")
    public ResponseEntity<List<PushNotificationLog>> getPushLogs() { return ResponseEntity.ok(service.getPushLogs()); }

    @GetMapping("/push/logs/user/{userId}")
    public ResponseEntity<List<PushNotificationLog>> getUserPushLogs(@PathVariable Long userId) { return ResponseEntity.ok(service.getUserPushLogs(userId)); }

    @PostMapping("/push/send")
    public ResponseEntity<PushNotificationLog> sendPush(@RequestBody PushNotificationLog notification) { return ResponseEntity.ok(service.sendPushNotification(notification)); }

    @PostMapping("/push/{id}/delivered")
    public ResponseEntity<PushNotificationLog> markDelivered(@PathVariable Long id) { return ResponseEntity.ok(service.markPushDelivered(id)); }

    @GetMapping("/push/stats")
    public ResponseEntity<Map<String, Object>> getPushStats() { return ResponseEntity.ok(service.getPushStats()); }

    // === App Config (Item 88) ===
    @GetMapping("/config")
    public ResponseEntity<List<MobileAppConfig>> getAppConfigs() { return ResponseEntity.ok(service.getAppConfigs()); }

    @PostMapping("/config")
    public ResponseEntity<MobileAppConfig> saveAppConfig(@RequestBody MobileAppConfig config) { return ResponseEntity.ok(service.createOrUpdateAppConfig(config)); }

    @GetMapping("/config/status")
    public ResponseEntity<Map<String, Object>> getAppStatus() { return ResponseEntity.ok(service.getAppStatus()); }

    // === Sync Conflicts (Item 89) ===
    @GetMapping("/conflicts")
    public ResponseEntity<List<SyncConflict>> getConflicts() { return ResponseEntity.ok(service.getConflicts()); }

    @GetMapping("/conflicts/pending")
    public ResponseEntity<List<SyncConflict>> getPendingConflicts() { return ResponseEntity.ok(service.getPendingConflicts()); }

    @PostMapping("/conflicts")
    public ResponseEntity<SyncConflict> createConflict(@RequestBody SyncConflict conflict) { return ResponseEntity.ok(service.createConflict(conflict)); }

    @PutMapping("/conflicts/{id}/resolve")
    public ResponseEntity<SyncConflict> resolveConflict(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        SyncConflict.ResolutionStrategy strategy = SyncConflict.ResolutionStrategy.valueOf((String) body.get("strategy"));
        Long resolvedBy = body.get("resolvedBy") != null ? ((Number) body.get("resolvedBy")).longValue() : null;
        return ResponseEntity.ok(service.resolveConflict(id, strategy, (String) body.get("resolvedData"), resolvedBy));
    }

    // === Usage Analytics (Item 90) ===
    @GetMapping("/usage")
    public ResponseEntity<List<MobileUsageStat>> getUsageStats() { return ResponseEntity.ok(service.getUsageStats()); }

    @GetMapping("/usage/user/{userId}")
    public ResponseEntity<List<MobileUsageStat>> getUserUsage(@PathVariable Long userId) { return ResponseEntity.ok(service.getUserUsageStats(userId)); }

    @PostMapping("/usage")
    public ResponseEntity<MobileUsageStat> recordUsage(@RequestBody MobileUsageStat stat) { return ResponseEntity.ok(service.recordUsage(stat)); }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getMobileAnalytics() { return ResponseEntity.ok(service.getMobileAnalytics()); }

    // === Offline Cache (Item 91) ===
    @GetMapping("/offline-cache/config")
    public ResponseEntity<Map<String, Object>> getOfflineCacheConfig() { return ResponseEntity.ok(service.getOfflineCacheConfig()); }

    // === Mobile Dashboard (Item 92) ===
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() { return ResponseEntity.ok(service.getMobileDashboard()); }

    // === Device Analytics (Item 93) ===
    @GetMapping("/devices/analytics")
    public ResponseEntity<Map<String, Object>> getDeviceAnalytics() { return ResponseEntity.ok(service.getDeviceAnalytics()); }

    // === Offline Package (Item 94) ===
    @GetMapping("/offline-package/{userId}")
    public ResponseEntity<Map<String, Object>> generateOfflinePackage(@PathVariable Long userId) { return ResponseEntity.ok(service.generateOfflinePackage(userId)); }
}
