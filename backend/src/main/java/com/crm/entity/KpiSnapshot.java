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
@Table(name = "kpi_snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "kpi_id", nullable = false)
    private Long kpiId;

    @Column(name = "actual_value")
    private BigDecimal actualValue;

    @Column(name = "target_value")
    private BigDecimal targetValue;

    @Column(name = "attainment_percentage")
    private Double attainmentPercentage;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @Column(name = "trend")
    private String trend;

    @Column(name = "previous_value")
    private BigDecimal previousValue;

    @Column(name = "change_percentage")
    private Double changePercentage;

    @Column(name = "status")
    private String status;

    @Column(name = "snapshot_at", updatable = false)
    private LocalDateTime snapshotAt;

    @PrePersist
    protected void onCreate() { snapshotAt = LocalDateTime.now(); }

    public enum Status { ON_TRACK, WARNING, AT_RISK, EXCEEDED }
}
