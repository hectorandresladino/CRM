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
@Table(name = "mobile_usage_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileUsageStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "stat_date")
    private LocalDateTime statDate;

    @Column(name = "session_count")
    private Integer sessionCount = 0;

    @Column(name = "total_session_minutes")
    private Integer totalSessionMinutes = 0;

    @Column(name = "features_used", columnDefinition = "TEXT")
    private String featuresUsed;

    @Column(name = "offline_actions")
    private Integer offlineActions = 0;

    @Column(name = "sync_count")
    private Integer syncCount = 0;

    @Column(name = "data_synced_bytes")
    private Long dataSyncedBytes = 0L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); if (statDate == null) statDate = LocalDateTime.now(); }
}
