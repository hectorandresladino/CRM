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
@Table(name = "customer_journeys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerJourney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "trigger_criteria", columnDefinition = "TEXT")
    private String triggerCriteria;

    @Column(name = "segment_id")
    private Long segmentId;

    @Column(name = "status")
    private String status = "DRAFT";

    @Column(name = "total_enrolled")
    private Integer totalEnrolled = 0;

    @Column(name = "total_completed")
    private Integer totalCompleted = 0;

    @Column(name = "total_converted")
    private Integer totalConverted = 0;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

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

    public enum TriggerType { SIGNUP, PURCHASE, ABANDONED_CART, PAGE_VIEW, EMAIL_OPEN, FIELD_CHANGE, SCHEDULE, WEBHOOK }
    public enum Status { DRAFT, ACTIVE, PAUSED, COMPLETED, ARCHIVED }
}
