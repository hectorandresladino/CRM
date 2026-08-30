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
@Table(name = "report_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "query_config", columnDefinition = "TEXT")
    private String queryConfig;

    @Column(name = "chart_type")
    private String chartType;

    @Column(name = "group_by")
    private String groupBy;

    @Column(name = "date_range_field")
    private String dateRangeField;

    @Column(name = "default_date_range")
    private String defaultDateRange;

    @Column(name = "columns", columnDefinition = "TEXT")
    private String columns;

    @Column(name = "filters", columnDefinition = "TEXT")
    private String filters;

    @Column(name = "sort_config", columnDefinition = "TEXT")
    private String sortConfig;

    @Column(name = "is_scheduled")
    private Boolean isScheduled = false;

    @Column(name = "schedule_cron")
    private String scheduleCron;

    @Column(name = "schedule_emails", columnDefinition = "TEXT")
    private String scheduleEmails;

    @Column(name = "format")
    private String format = "PDF";

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "is_shared")
    private Boolean isShared = false;

    @Column(name = "folder_id")
    private Long folderId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum ReportType { TABULAR, SUMMARY, MATRIX, JOINED, CHART, PIVOT }
}
