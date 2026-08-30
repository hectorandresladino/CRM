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
@Table(name = "analytics_dashboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "dashboard_type")
    private String dashboardType;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "is_shared")
    private Boolean isShared = false;

    @Column(name = "shared_with")
    private String sharedWith;

    @Column(name = "widgets", columnDefinition = "TEXT")
    private String widgets;

    @Column(name = "filters", columnDefinition = "TEXT")
    private String filters;

    @Column(name = "refresh_frequency")
    private String refreshFrequency = "MANUAL";

    @Column(name = "last_refreshed_at")
    private LocalDateTime lastRefreshedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum DashboardType { SALES, MARKETING, SERVICE, FINANCIAL, EXECUTIVE, CUSTOM }
    public enum RefreshFrequency { MANUAL, HOURLY, DAILY, WEEKLY, REAL_TIME }
}
