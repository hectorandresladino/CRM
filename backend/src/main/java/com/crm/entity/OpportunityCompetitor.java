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
@Table(name = "opportunity_competitors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityCompetitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(nullable = false)
    private String competitorName;

    private String strengths;
    private String weaknesses;

    @Column(name = "threat_level")
    private String threatLevel;

    @Column(name = "our_position")
    private String ourPosition;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
