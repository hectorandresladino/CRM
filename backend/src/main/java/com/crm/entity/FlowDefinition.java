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
@Table(name = "flow_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "flow_type", nullable = false)
    private String flowType;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "trigger_object")
    private String triggerObject;

    @Column(name = "trigger_condition")
    @Lob
    private String triggerCondition;

    @Column(name = "flow_steps")
    @Lob
    private String flowSteps;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "run_count")
    private Integer runCount = 0;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum FlowType { RECORD_TRIGGERED, SCHEDULED, SCREEN_FLOW, AUTO_LAUNCHED, PLATFORM_EVENT }
    public enum TriggerType { CREATE, UPDATE, DELETE, SCHEDULED, PLATFORM_EVENT, MANUAL }
}
