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
@Table(name = "commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sale_id")
    private Long saleId;

    @Column(name = "commission_plan_id")
    private Long commissionPlanId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "commission_rate")
    private BigDecimal commissionRate;

    @Column(name = "sale_amount")
    private BigDecimal saleAmount;

    @Column(nullable = false)
    private String status = "CALCULATED";

    @Column(name = "period_year")
    private Integer periodYear;

    @Column(name = "period_month")
    private Integer periodMonth;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum Status { CALCULATED, APPROVED, PAID, DISPUTED, REVERSED }
}
