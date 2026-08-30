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
@Table(name = "service_milestones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "entitlement_id", nullable = false)
    private Long entitlementId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(nullable = false)
    private String name;

    @Column(name = "milestone_type")
    private String milestoneType;

    @Column(name = "target_minutes")
    private Integer targetMinutes;

    @Column(name = "actual_minutes")
    private Integer actualMinutes;

    @Column(name = "is_violated")
    private Boolean isViolated = false;

    @Column(name = "is_achieved")
    private Boolean isAchieved = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "target_time")
    private LocalDateTime targetTime;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum MilestoneType { FIRST_RESPONSE, RESOLUTION, ESCALATION, FOLLOW_UP }
}
