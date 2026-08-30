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
@Table(name = "journey_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourneyStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "journey_id", nullable = false)
    private Long journeyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StepType type;

    @Column(name = "delay_hours")
    private Integer delayHours = 0;

    @Column(name = "action_config", columnDefinition = "TEXT")
    private String actionConfig;

    @Column(name = "condition_config", columnDefinition = "TEXT")
    private String conditionConfig;

    @Column(name = "next_step_id")
    private Long nextStepId;

    @Column(name = "branch_a_step_id")
    private Long branchAStepId;

    @Column(name = "branch_b_step_id")
    private Long branchBStepId;

    @Column(name = "order_num")
    private Integer orderNum = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum StepType { EMAIL, SMS, WHATSAPP, WAIT, CONDITION, WEBHOOK, UPDATE_FIELD, TAG, NOTIFY, END }
}
