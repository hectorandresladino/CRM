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
@Table(name = "ab_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ABTest {

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
    private TestType type;

    @Column(name = "variant_a_id")
    private Long variantAId;

    @Column(name = "variant_b_id")
    private Long variantBId;

    @Column(name = "variant_a_visits")
    private Integer variantAVisits = 0;

    @Column(name = "variant_b_visits")
    private Integer variantBVisits = 0;

    @Column(name = "variant_a_conversions")
    private Integer variantAConversions = 0;

    @Column(name = "variant_b_conversions")
    private Integer variantBConversions = 0;

    @Column(name = "variant_a_rate")
    private Double variantARate = 0.0;

    @Column(name = "variant_b_rate")
    private Double variantBRate = 0.0;

    @Column(name = "confidence_level")
    private Double confidenceLevel = 0.0;

    @Column(name = "winning_variant")
    private String winningVariant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestStatus status = TestStatus.RUNNING;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); if (startedAt == null) startedAt = LocalDateTime.now(); }

    public enum TestType { LANDING_PAGE, EMAIL_SUBJECT, EMAIL_CONTENT, AD_COPY, CTA_BUTTON, LAYOUT, IMAGE }
    public enum TestStatus { DRAFT, RUNNING, PAUSED, COMPLETED, ARCHIVED }
}
