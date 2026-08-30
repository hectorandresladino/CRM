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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MobileOfflineService {

    private final MobileDeviceRepository deviceRepo;
    private final OfflineSyncQueueRepository syncQueueRepo;
    private final PushNotificationLogRepository pushLogRepo;
    private final MobileAppConfigRepository appConfigRepo;
    private final SyncConflictRepository conflictRepo;
    private final MobileUsageStatRepository usageStatRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Mobile Device Management (Item 85) ===

    public List<MobileDevice> getDevices() { return deviceRepo.findByTenantId(tid()); }

    public List<MobileDevice> getUserDevices(Long userId) { return deviceRepo.findByTenantIdAndUserId(tid(), userId); }

    public MobileDevice registerDevice(MobileDevice device) {
        device.setTenantId(tid());
        device.setIsRegistered(true);
        device.setLastSeenAt(LocalDateTime.now());
        return deviceRepo.findByTenantIdAndDeviceUuid(tid(), device.getDeviceUuid())
                .map(existing -> {
                    existing.setDeviceName(device.getDeviceName());
                    existing.setDeviceModel(device.getDeviceModel());
                    existing.setOsType(device.getOsType());
                    existing.setOsVersion(device.getOsVersion());
                    existing.setAppVersion(device.getAppVersion());
                    existing.setPushToken(device.getPushToken());
                    existing.setLastSeenAt(LocalDateTime.now());
                    return deviceRepo.save(existing);
                })
                .orElseGet(() -> deviceRepo.save(device));
    }

    public MobileDevice unregisterDevice(Long id) {
        MobileDevice device = deviceRepo.findById(id).orElseThrow();
        device.setIsRegistered(false);
        return deviceRepo.save(device);
    }

    public MobileDevice updateDeviceLocation(Long id, String pushToken) {
        MobileDevice device = deviceRepo.findById(id).orElseThrow();
        device.setPushToken(pushToken);
        device.setLastSeenAt(LocalDateTime.now());
        return deviceRepo.save(device);
    }

    // === Offline Sync Queue (Item 86) ===

    public List<OfflineSyncQueue> getSyncQueue() { return syncQueueRepo.findByTenantId(tid()); }

    public List<OfflineSyncQueue> getPendingSyncItems() {
        return syncQueueRepo.findByTenantIdAndStatus(tid(), OfflineSyncQueue.SyncStatus.PENDING);
    }

    public OfflineSyncQueue queueOfflineOperation(OfflineSyncQueue item) {
        item.setTenantId(tid());
        item.setStatus(OfflineSyncQueue.SyncStatus.PENDING);
        return syncQueueRepo.save(item);
    }

    public OfflineSyncQueue processSyncItem(Long id) {
        OfflineSyncQueue item = syncQueueRepo.findById(id).orElseThrow();
        item.setStatus(OfflineSyncQueue.SyncStatus.SYNCING);
        item.setAttempts(item.getAttempts() + 1);
        item = syncQueueRepo.save(item);

        try {
            item.setStatus(OfflineSyncQueue.SyncStatus.COMPLETED);
            item.setSyncedAt(LocalDateTime.now());
        } catch (Exception e) {
            if (item.getAttempts() >= item.getMaxAttempts()) {
                item.setStatus(OfflineSyncQueue.SyncStatus.FAILED);
            } else {
                item.setStatus(OfflineSyncQueue.SyncStatus.PENDING);
            }
            item.setErrorMessage(e.getMessage());
        }
        return syncQueueRepo.save(item);
    }

    public Map<String, Object> getSyncStats() {
        List<OfflineSyncQueue> items = syncQueueRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalItems", items.size());
        stats.put("pending", items.stream().filter(i -> i.getStatus() == OfflineSyncQueue.SyncStatus.PENDING).count());
        stats.put("completed", items.stream().filter(i -> i.getStatus() == OfflineSyncQueue.SyncStatus.COMPLETED).count());
        stats.put("failed", items.stream().filter(i -> i.getStatus() == OfflineSyncQueue.SyncStatus.FAILED).count());
        stats.put("conflicts", items.stream().filter(i -> i.getStatus() == OfflineSyncQueue.SyncStatus.CONFLICT).count());

        Map<String, Long> byOperation = new LinkedHashMap<>();
        for (OfflineSyncQueue i : items) {
            String op = i.getOperation() != null ? i.getOperation() : "UNKNOWN";
            byOperation.merge(op, 1L, Long::sum);
        }
        stats.put("byOperation", byOperation);
        return stats;
    }

    // === Push Notifications (Item 87) ===

    public PushNotificationLog sendPushNotification(PushNotificationLog notification) {
        notification.setTenantId(tid());
        notification.setStatus(PushNotificationLog.DeliveryStatus.SENT);
        notification.setProviderMessageId("msg-" + UUID.randomUUID().toString().substring(0, 8));
        return pushLogRepo.save(notification);
    }

    public List<PushNotificationLog> getPushLogs() { return pushLogRepo.findByTenantId(tid()); }

    public List<PushNotificationLog> getUserPushLogs(Long userId) { return pushLogRepo.findByTenantIdAndUserId(tid(), userId); }

    public PushNotificationLog markPushDelivered(Long id) {
        PushNotificationLog log = pushLogRepo.findById(id).orElseThrow();
        log.setStatus(PushNotificationLog.DeliveryStatus.DELIVERED);
        log.setDeliveredAt(LocalDateTime.now());
        return pushLogRepo.save(log);
    }

    public Map<String, Object> getPushStats() {
        List<PushNotificationLog> logs = pushLogRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalSent", logs.size());
        stats.put("delivered", logs.stream().filter(l -> l.getStatus() == PushNotificationLog.DeliveryStatus.DELIVERED).count());
        stats.put("failed", logs.stream().filter(l -> l.getStatus() == PushNotificationLog.DeliveryStatus.FAILED).count());
        stats.put("deliveryRate", logs.size() > 0
                ? (double) logs.stream().filter(l -> l.getStatus() == PushNotificationLog.DeliveryStatus.DELIVERED).count() / logs.size() * 100 : 0);
        return stats;
    }

    // === Mobile App Configuration (Item 88) ===

    public List<MobileAppConfig> getAppConfigs() { return appConfigRepo.findByTenantId(tid()); }

    public MobileAppConfig createOrUpdateAppConfig(MobileAppConfig config) {
        config.setTenantId(tid());
        return appConfigRepo.findByTenantIdAndConfigKey(tid(), config.getConfigKey())
                .map(existing -> {
                    existing.setConfigValue(config.getConfigValue());
                    existing.setMinAppVersion(config.getMinAppVersion());
                    existing.setForceUpdate(config.getForceUpdate());
                    existing.setMaintenanceMode(config.getMaintenanceMode());
                    existing.setMaintenanceMessage(config.getMaintenanceMessage());
                    existing.setOfflineCacheDays(config.getOfflineCacheDays());
                    existing.setMaxOfflineRecords(config.getMaxOfflineRecords());
                    existing.setSyncIntervalSeconds(config.getSyncIntervalSeconds());
                    existing.setIsActive(config.getIsActive());
                    return appConfigRepo.save(existing);
                })
                .orElseGet(() -> appConfigRepo.save(config));
    }

    public Map<String, Object> getAppStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        List<MobileAppConfig> configs = appConfigRepo.findByTenantIdAndIsActive(tid(), true);
        boolean maintenance = configs.stream().anyMatch(c -> Boolean.TRUE.equals(c.getMaintenanceMode()));
        status.put("maintenanceMode", maintenance);
        status.put("forceUpdateRequired", configs.stream().anyMatch(c -> Boolean.TRUE.equals(c.getForceUpdate())));
        configs.stream().filter(c -> c.getMaintenanceMessage() != null)
                .findFirst().ifPresent(c -> status.put("maintenanceMessage", c.getMaintenanceMessage()));
        configs.stream().filter(c -> c.getMinAppVersion() != null)
                .findFirst().ifPresent(c -> status.put("minAppVersion", c.getMinAppVersion()));
        configs.stream().filter(c -> c.getOfflineCacheDays() != null)
                .findFirst().ifPresent(c -> status.put("offlineCacheDays", c.getOfflineCacheDays()));
        configs.stream().filter(c -> c.getSyncIntervalSeconds() != null)
                .findFirst().ifPresent(c -> status.put("syncIntervalSeconds", c.getSyncIntervalSeconds()));
        return status;
    }

    // === Sync Conflict Resolution (Item 89) ===

    public List<SyncConflict> getConflicts() { return conflictRepo.findByTenantId(tid()); }

    public List<SyncConflict> getPendingConflicts() {
        return conflictRepo.findByTenantIdAndResolution(tid(), SyncConflict.ResolutionStrategy.PENDING);
    }

    public SyncConflict createConflict(SyncConflict conflict) {
        conflict.setTenantId(tid());
        conflict.setResolution(SyncConflict.ResolutionStrategy.PENDING);
        return conflictRepo.save(conflict);
    }

    public SyncConflict resolveConflict(Long id, SyncConflict.ResolutionStrategy strategy, String resolvedData, Long resolvedBy) {
        SyncConflict conflict = conflictRepo.findById(id).orElseThrow();
        conflict.setResolution(strategy);
        if (resolvedData != null) conflict.setResolvedData(resolvedData);
        conflict.setResolvedBy(resolvedBy);
        conflict.setResolvedAt(LocalDateTime.now());
        return conflictRepo.save(conflict);
    }

    // === Mobile Usage Analytics (Item 90) ===

    public List<MobileUsageStat> getUsageStats() { return usageStatRepo.findByTenantId(tid()); }

    public List<MobileUsageStat> getUserUsageStats(Long userId) { return usageStatRepo.findByTenantIdAndUserId(tid(), userId); }

    public MobileUsageStat recordUsage(MobileUsageStat stat) {
        stat.setTenantId(tid());
        return usageStatRepo.save(stat);
    }

    public Map<String, Object> getMobileAnalytics() {
        List<MobileUsageStat> stats = usageStatRepo.findByTenantId(tid());
        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalSessions", stats.stream().mapToInt(s -> s.getSessionCount() != null ? s.getSessionCount() : 0).sum());
        analytics.put("totalSessionMinutes", stats.stream().mapToInt(s -> s.getTotalSessionMinutes() != null ? s.getTotalSessionMinutes() : 0).sum());
        analytics.put("totalOfflineActions", stats.stream().mapToInt(s -> s.getOfflineActions() != null ? s.getOfflineActions() : 0).sum());
        analytics.put("totalSyncs", stats.stream().mapToInt(s -> s.getSyncCount() != null ? s.getSyncCount() : 0).sum());
        long totalDataSynced = stats.stream()
                .mapToLong(s -> s.getDataSyncedBytes() != null ? s.getDataSyncedBytes() : 0)
                .sum();
        analytics.put("totalDataSyncedMB", totalDataSynced / (1024.0 * 1024.0));
        return analytics;
    }

    // === Offline Cache Management (Item 91) ===

    public Map<String, Object> getOfflineCacheConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        List<MobileAppConfig> configs = appConfigRepo.findByTenantIdAndIsActive(tid(), true);
        configs.stream().filter(c -> c.getOfflineCacheDays() != null)
                .findFirst().ifPresent(c -> config.put("cacheDays", c.getOfflineCacheDays()));
        configs.stream().filter(c -> c.getMaxOfflineRecords() != null)
                .findFirst().ifPresent(c -> config.put("maxRecords", c.getMaxOfflineRecords()));
        config.put("pendingSyncItems", syncQueueRepo.findByTenantIdAndStatus(tid(), OfflineSyncQueue.SyncStatus.PENDING).size());
        config.put("conflictItems", conflictRepo.findByTenantIdAndResolution(tid(), SyncConflict.ResolutionStrategy.PENDING).size());
        return config;
    }

    // === Mobile Dashboard (Item 92) ===

    public Map<String, Object> getMobileDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("registeredDevices", deviceRepo.findByTenantIdAndIsRegistered(tid(), true).size());
        dashboard.put("totalDevices", deviceRepo.findByTenantId(tid()).size());
        dashboard.put("pendingSyncItems", syncQueueRepo.findByTenantIdAndStatus(tid(), OfflineSyncQueue.SyncStatus.PENDING).size());
        dashboard.put("failedSyncItems", syncQueueRepo.findByTenantIdAndStatus(tid(), OfflineSyncQueue.SyncStatus.FAILED).size());
        dashboard.put("pendingConflicts", conflictRepo.findByTenantIdAndResolution(tid(), SyncConflict.ResolutionStrategy.PENDING).size());
        dashboard.put("pushStats", getPushStats());
        dashboard.put("syncStats", getSyncStats());
        dashboard.put("appStatus", getAppStatus());
        dashboard.put("mobileAnalytics", getMobileAnalytics());
        dashboard.put("offlineCacheConfig", getOfflineCacheConfig());
        return dashboard;
    }

    // === Device Analytics by Platform (Item 93) ===

    public Map<String, Object> getDeviceAnalytics() {
        List<MobileDevice> devices = deviceRepo.findByTenantId(tid());
        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalDevices", devices.size());

        Map<String, Long> byOs = new LinkedHashMap<>();
        for (MobileDevice d : devices) {
            String os = d.getOsType() != null ? d.getOsType() : "UNKNOWN";
            byOs.merge(os, 1L, Long::sum);
        }
        analytics.put("byOsType", byOs);

        Map<String, Long> byVersion = new LinkedHashMap<>();
        for (MobileDevice d : devices) {
            String ver = d.getAppVersion() != null ? d.getAppVersion() : "UNKNOWN";
            byVersion.merge(ver, 1L, Long::sum);
        }
        analytics.put("byAppVersion", byVersion);

        long activeDevices = devices.stream()
                .filter(d -> d.getLastSeenAt() != null && d.getLastSeenAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
        analytics.put("activeDevices7d", activeDevices);
        return analytics;
    }

    // === Offline Data Package (Item 94) ===

    public Map<String, Object> generateOfflinePackage(Long userId) {
        Map<String, Object> pkg = new LinkedHashMap<>();
        pkg.put("userId", userId);
        pkg.put("generatedAt", LocalDateTime.now().toString());

        List<MobileAppConfig> configs = appConfigRepo.findByTenantIdAndIsActive(tid(), true);
        configs.stream().filter(c -> c.getOfflineCacheDays() != null)
                .findFirst().ifPresent(c -> pkg.put("cacheDays", c.getOfflineCacheDays()));
        configs.stream().filter(c -> c.getMaxOfflineRecords() != null)
                .findFirst().ifPresent(c -> pkg.put("maxRecords", c.getMaxOfflineRecords()));

        List<OfflineSyncQueue> pending = syncQueueRepo.findByTenantIdAndUserId(tid(), userId);
        pkg.put("pendingOperations", pending.size());

        List<MobileDevice> devices = deviceRepo.findByTenantIdAndUserId(tid(), userId);
        pkg.put("deviceCount", devices.size());

        pkg.put("appStatus", getAppStatus());
        return pkg;
    }
}
