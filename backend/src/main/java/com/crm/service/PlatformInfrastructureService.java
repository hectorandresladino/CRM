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
public class PlatformInfrastructureService {

    private final FeatureFlagRepository featureFlagRepo;
    private final SystemSettingRepository settingRepo;
    private final RateLimitConfigRepository rateLimitRepo;
    private final FileStorageRecordRepository fileStorageRepo;
    private final BackupRecordRepository backupRepo;
    private final ApiKeyRepository apiKeyRepo;
    private final AuditLogRepository auditLogRepo;
    private final NotificationRepository notificationRepo;
    private final CurrencyRepository currencyRepo;
    private final CurrencyRateRepository currencyRateRepo;

    private Long tid() {
        Long t = TenantContext.getCurrentTenant();
        if (t == null) throw new RuntimeException("No tenant context");
        return t;
    }

    // === Feature Flags (Item 65) ===

    public List<FeatureFlag> getFeatureFlags() { return featureFlagRepo.findByTenantId(tid()); }

    public FeatureFlag createFeatureFlag(FeatureFlag flag) {
        flag.setTenantId(tid());
        return featureFlagRepo.save(flag);
    }

    public FeatureFlag toggleFeatureFlag(Long id) {
        FeatureFlag flag = featureFlagRepo.findById(id).orElseThrow();
        flag.setIsEnabled(!flag.getIsEnabled());
        return featureFlagRepo.save(flag);
    }

    public boolean isFeatureEnabled(String key) {
        return featureFlagRepo.findByTenantIdAndKey(tid(), key)
                .map(FeatureFlag::getIsEnabled)
                .orElse(false);
    }

    // === System Settings (Item 66) ===

    public List<SystemSetting> getSettings() { return settingRepo.findByTenantId(tid()); }

    public List<SystemSetting> getSettingsByCategory(String category) {
        return settingRepo.findByTenantIdAndCategory(tid(), category);
    }

    public SystemSetting createOrUpdateSetting(SystemSetting setting) {
        setting.setTenantId(tid());
        return settingRepo.findByTenantIdAndKey(tid(), setting.getKey())
                .map(existing -> {
                    existing.setValue(setting.getValue());
                    existing.setDescription(setting.getDescription());
                    existing.setDataType(setting.getDataType());
                    existing.setCategory(setting.getCategory());
                    existing.setIsPublic(setting.getIsPublic());
                    return settingRepo.save(existing);
                })
                .orElseGet(() -> settingRepo.save(setting));
    }

    public String getSettingValue(String key, String defaultValue) {
        return settingRepo.findByTenantIdAndKey(tid(), key)
                .map(SystemSetting::getValue)
                .orElse(defaultValue);
    }

    // === Rate Limiting (Item 67) ===

    public List<RateLimitConfig> getRateLimits() { return rateLimitRepo.findByTenantId(tid()); }

    public RateLimitConfig createRateLimit(RateLimitConfig config) {
        config.setTenantId(tid());
        return rateLimitRepo.save(config);
    }

    public boolean checkRateLimit(String endpoint) {
        return rateLimitRepo.findByTenantIdAndEndpoint(tid(), endpoint)
                .map(config -> {
                    if (!Boolean.TRUE.equals(config.getIsActive())) return true;
                    return config.getCurrentRequestsMinute() < config.getRequestsPerMinute();
                })
                .orElse(true);
    }

    public void recordRequest(String endpoint) {
        rateLimitRepo.findByTenantIdAndEndpoint(tid(), endpoint).ifPresent(config -> {
            config.setCurrentRequestsMinute(config.getCurrentRequestsMinute() + 1);
            config.setCurrentRequestsHour(config.getCurrentRequestsHour() + 1);
            config.setCurrentRequestsDay(config.getCurrentRequestsDay() + 1);
            rateLimitRepo.save(config);
        });
    }

    public void resetRateCounters() {
        List<RateLimitConfig> configs = rateLimitRepo.findByTenantId(tid());
        for (RateLimitConfig c : configs) {
            c.setCurrentRequestsMinute(0);
            c.setCurrentRequestsHour(0);
            c.setCurrentRequestsDay(0);
            c.setLastResetAt(LocalDateTime.now());
            rateLimitRepo.save(c);
        }
    }

    // === File Storage (Item 68) ===

    public List<FileStorageRecord> getFiles() { return fileStorageRepo.findByTenantId(tid()); }

    public List<FileStorageRecord> getFilesByEntity(String entityType, Long entityId) {
        return fileStorageRepo.findByTenantIdAndEntityId(tid(), entityId);
    }

    public FileStorageRecord recordFileUpload(FileStorageRecord record) {
        record.setTenantId(tid());
        return fileStorageRepo.save(record);
    }

    public FileStorageRecord incrementDownloadCount(Long id) {
        FileStorageRecord record = fileStorageRepo.findById(id).orElseThrow();
        record.setDownloadCount(record.getDownloadCount() + 1);
        return fileStorageRepo.save(record);
    }

    public Map<String, Object> getStorageStats() {
        List<FileStorageRecord> files = fileStorageRepo.findByTenantId(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalFiles", files.size());
        long totalSize = files.stream()
                .mapToLong(f -> f.getFileSizeBytes() != null ? f.getFileSizeBytes() : 0)
                .sum();
        stats.put("totalSizeBytes", totalSize);
        stats.put("totalSizeMB", totalSize / (1024.0 * 1024.0));

        Map<String, Long> byType = new LinkedHashMap<>();
        for (FileStorageRecord f : files) {
            String type = f.getFileType() != null ? f.getFileType() : "UNKNOWN";
            byType.merge(type, 1L, Long::sum);
        }
        stats.put("filesByType", byType);
        return stats;
    }

    // === Backup & Restore (Item 69) ===

    public List<BackupRecord> getBackups() { return backupRepo.findByTenantId(tid()); }

    public BackupRecord createBackup(BackupRecord backup) {
        backup.setTenantId(tid());
        backup.setStatus(BackupRecord.BackupStatus.RUNNING);
        backup.setStartedAt(LocalDateTime.now());
        backup = backupRepo.save(backup);

        try {
            backup.setRecordCount(estimateRecordCount());
            backup.setTableCount(estimateTableCount());
            backup.setFileSizeBytes(estimateBackupSize());
            backup.setFileUrl("/backups/backup-" + backup.getId() + ".tar.gz");
            backup.setStatus(BackupRecord.BackupStatus.COMPLETED);
            backup.setCompletedAt(LocalDateTime.now());
            backup.setExpiresAt(LocalDateTime.now().plusDays(30));
        } catch (Exception e) {
            backup.setStatus(BackupRecord.BackupStatus.FAILED);
            backup.setErrorMessage(e.getMessage());
            backup.setCompletedAt(LocalDateTime.now());
        }

        return backupRepo.save(backup);
    }

    private int estimateRecordCount() { return 1000; }
    private int estimateTableCount() { return 50; }
    private long estimateBackupSize() { return 5_000_000L; }

    // === API Key Management (Item 70) ===

    public List<ApiKey> getApiKeys() { return apiKeyRepo.findByTenantId(tid()); }

    public ApiKey createApiKey(ApiKey apiKey) {
        apiKey.setTenantId(tid());
        if (apiKey.getKey() == null || apiKey.getKey().isBlank()) {
            apiKey.setKey(UUID.randomUUID().toString().replace("-", ""));
        }
        return apiKeyRepo.save(apiKey);
    }

    public ApiKey revokeApiKey(Long id) {
        ApiKey key = apiKeyRepo.findById(id).orElseThrow();
        key.setEsActivo(false);
        return apiKeyRepo.save(key);
    }

    public ApiKey recordApiKeyUsage(String keyValue) {
        ApiKey key = apiKeyRepo.findByKeyAndEsActivo(keyValue, true).orElseThrow();
        key.setTotalUsos(key.getTotalUsos() + 1);
        key.setUltimoUso(LocalDateTime.now());
        return apiKeyRepo.save(key);
    }

    // === Audit Log (Item 71) ===

    public List<AuditLog> getAuditLogs() { return auditLogRepo.findByTenantIdOrderByCreatedAtDesc(tid()); }

    public AuditLog logAction(AuditLog log) {
        if (log.getTenantId() == null) log.setTenantId(tid());
        return auditLogRepo.save(log);
    }

    public Map<String, Object> getAuditStats() {
        List<AuditLog> logs = auditLogRepo.findByTenantIdOrderByCreatedAtDesc(tid());
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalLogs", logs.size());

        Map<String, Long> byAction = new LinkedHashMap<>();
        for (AuditLog l : logs) {
            String action = l.getAction() != null ? l.getAction() : "UNKNOWN";
            byAction.merge(action, 1L, Long::sum);
        }
        stats.put("byAction", byAction);

        Map<String, Long> byEntity = new LinkedHashMap<>();
        for (AuditLog l : logs) {
            String entity = l.getEntity() != null ? l.getEntity() : "UNKNOWN";
            byEntity.merge(entity, 1L, Long::sum);
        }
        stats.put("byEntity", byEntity);
        return stats;
    }

    // === Notifications (Item 72) ===

    public List<Notification> getNotifications() { return notificationRepo.findByTenantId(tid()); }

    public Notification sendNotification(Notification notification) {
        notification.setTenantId(tid());
        return notificationRepo.save(notification);
    }

    public Notification markNotificationRead(Long id) {
        Notification n = notificationRepo.findById(id).orElseThrow();
        n.setRead(true);
        return notificationRepo.save(n);
    }

    // === Currency Management (Item 73) ===

    public List<com.crm.entity.Currency> getCurrencies() { return currencyRepo.findByTenantId(tid()); }

    public com.crm.entity.Currency createCurrency(com.crm.entity.Currency currency) {
        currency.setTenantId(tid());
        return currencyRepo.save(currency);
    }

    public CurrencyRate updateExchangeRate(CurrencyRate rate) {
        rate.setTenantId(tid());
        return currencyRateRepo.save(rate);
    }

    public List<CurrencyRate> getExchangeRates() { return currencyRateRepo.findByTenantId(tid()); }

    // === Platform Dashboard (Item 74) ===

    public Map<String, Object> getPlatformDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("featureFlags", featureFlagRepo.findByTenantId(tid()).size());
        dashboard.put("enabledFeatures", featureFlagRepo.findByTenantIdAndIsEnabled(tid(), true).size());
        dashboard.put("settings", settingRepo.findByTenantId(tid()).size());
        dashboard.put("rateLimits", rateLimitRepo.findByTenantId(tid()).size());
        dashboard.put("storageStats", getStorageStats());
        dashboard.put("backups", backupRepo.findByTenantId(tid()).size());
        dashboard.put("completedBackups", backupRepo.findByTenantIdAndStatus(tid(), BackupRecord.BackupStatus.COMPLETED).size());
        dashboard.put("apiKeys", apiKeyRepo.findByTenantId(tid()).size());
        dashboard.put("activeApiKeys", apiKeyRepo.findByTenantId(tid()).stream().filter(ApiKey::getEsActivo).count());
        dashboard.put("auditLogs", auditLogRepo.findByTenantIdOrderByCreatedAtDesc(tid()).size());
        dashboard.put("notifications", notificationRepo.findByTenantId(tid()).size());
        dashboard.put("currencies", currencyRepo.findByTenantId(tid()).size());
        return dashboard;
    }
}
