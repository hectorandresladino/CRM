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
@Table(name = "opportunity_splits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitySplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "split_percentage", nullable = false)
    private BigDecimal splitPercentage;

    @Column(name = "split_amount")
    private BigDecimal splitAmount;

    @Column(name = "split_type")
    private String splitType = "REVENUE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum SplitType { REVENUE, OVERLAY, PIPELINE }
}
