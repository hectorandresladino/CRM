/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mobile_app_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileAppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "config_key")
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "min_app_version")
    private String minAppVersion;

    @Column(name = "force_update")
    private Boolean forceUpdate = false;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;

    @Column(name = "maintenance_message")
    private String maintenanceMessage;

    @Column(name = "offline_cache_days")
    private Integer offlineCacheDays = 7;

    @Column(name = "max_offline_records")
    private Integer maxOfflineRecords = 1000;

    @Column(name = "sync_interval_seconds")
    private Integer syncIntervalSeconds = 60;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
