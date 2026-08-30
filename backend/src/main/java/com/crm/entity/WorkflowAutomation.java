package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_automations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowAutomation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String triggerType;

    @Column(columnDefinition = "TEXT")
    private String triggerConfig;

    @Column(nullable = false)
    private String actionType;

    @Column(columnDefinition = "TEXT")
    private String actionConfig;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum TriggerType {
        NEW_PROSPECTO, PROSPECTO_STAGE_CHANGE, NEW_VENTA, VENTA_STAGE_CHANGE,
        NEW_CLIENTE, NEW_TICKET, TICKET_ESCALATION, SCHEDULED, WEBHOOK
    }

    public enum ActionType {
        SEND_EMAIL, SEND_WHATSAPP, CREATE_TASK, UPDATE_FIELD, NOTIFY_USER,
        CREATE_TICKET, MOVE_STAGE, WEBHOOK_CALL
    }
}
