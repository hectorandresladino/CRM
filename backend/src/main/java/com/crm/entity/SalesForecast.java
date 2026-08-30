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
@Table(name = "sales_forecasts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "territory_id")
    private Long territoryId;

    @Column(name = "period_type", nullable = false)
    private String periodType;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_quarter")
    private Integer periodQuarter;

    @Column(name = "period_month")
    private Integer periodMonth;

    @Column(name = "forecast_amount")
    private BigDecimal forecastAmount;

    @Column(name = "commit_amount")
    private BigDecimal commitAmount;

    @Column(name = "best_case_amount")
    private BigDecimal bestCaseAmount;

    @Column(name = "closed_amount")
    private BigDecimal closedAmount;

    @Column(name = "pipeline_amount")
    private BigDecimal pipelineAmount;

    @Column(name = "forecast_category")
    private String forecastCategory;

    @Column(name = "status")
    private String status = "DRAFT";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum PeriodType { MONTHLY, QUARTERLY, YEARLY }
    public enum ForecastCategory { PIPELINE, BEST_CASE, COMMIT, CLOSED, OMITTED }
    public enum Status { DRAFT, SUBMITTED, APPROVED, ADJUSTED }
}
