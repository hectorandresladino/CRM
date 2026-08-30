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
@Table(name = "flow_execution_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "flow_id", nullable = false)
    private Long flowId;

    @Column(name = "flow_name")
    private String flowName;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "trigger_entity")
    private String triggerEntity;

    @Column(name = "trigger_entity_id")
    private Long triggerEntityId;

    @Column(name = "trigger_data", columnDefinition = "TEXT")
    private String triggerData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status = ExecutionStatus.RUNNING;

    @Column(name = "steps_completed")
    private Integer stepsCompleted = 0;

    @Column(name = "steps_total")
    private Integer stepsTotal = 0;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "error_step")
    private String errorStep;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public enum ExecutionStatus { RUNNING, COMPLETED, FAILED, CANCELLED, TIMEOUT }
}
