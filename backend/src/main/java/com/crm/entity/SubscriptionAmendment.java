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
@Table(name = "subscription_amendments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAmendment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(nullable = false)
    private String amendmentType;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "old_plan_id")
    private Long oldPlanId;

    @Column(name = "new_plan_id")
    private Long newPlanId;

    @Column(name = "old_amount")
    private BigDecimal oldAmount;

    @Column(name = "new_amount")
    private BigDecimal newAmount;

    @Column(name = "proration_amount")
    private BigDecimal prorationAmount;

    @Column(name = "old_quantity")
    private Integer oldQuantity;

    @Column(name = "new_quantity")
    private Integer newQuantity;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "reason")
    private String reason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum AmendmentType { UPGRADE, DOWNGRADE, ADD_SEAT, REMOVE_SEAT, RENEW, CANCEL, PAUSE, RESUME }
    public enum Status { PENDING, PROCESSED, FAILED, REVERSED }
}
