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
@Table(name = "customer_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "segment_type")
    private String segmentType;

    @Column(name = "criteria")
    @Lob
    private String criteria;

    @Column(name = "member_count")
    private Integer memberCount = 0;

    @Column(name = "is_dynamic")
    private Boolean isDynamic = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum SegmentType { DEMOGRAPHIC, BEHAVIORAL, TRANSACTIONAL, ENGAGEMENT, CHURN_RISK, HIGH_VALUE, NEW_CUSTOMER, LOYAL }
}
