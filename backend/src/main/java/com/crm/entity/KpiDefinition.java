/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kpi_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "metric_name")
    private String metricName;

    @Column(name = "aggregation_type")
    private String aggregationType;

    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "target_value")
    private BigDecimal targetValue;

    @Column(name = "warning_threshold")
    private BigDecimal warningThreshold;

    @Column(name = "unit")
    private String unit;

    @Column(name = "period_type")
    private String periodType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum PeriodType { DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY }
}
