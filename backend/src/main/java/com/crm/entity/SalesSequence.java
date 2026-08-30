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
@Table(name = "sales_sequences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "target_audience")
    private String targetAudience;

    @Column(name = "step_count")
    private Integer stepCount;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "enrolled_count")
    private Integer enrolledCount = 0;

    @Column(name = "reply_rate")
    private Double replyRate;

    @Column(name = "meeting_rate")
    private Double meetingRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
