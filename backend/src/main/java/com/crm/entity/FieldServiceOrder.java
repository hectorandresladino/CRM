package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "field_service_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(nullable = false)
    private String title;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "assigned_technician")
    private String assignedTechnician;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "estimated_duration_min")
    private Integer estimatedDurationMin;

    @Column(name = "actual_duration_min")
    private Integer actualDurationMin;

    @Column(name = "service_address")
    private String serviceAddress;

    @Column(name = "service_lat")
    private BigDecimal serviceLat;

    @Column(name = "service_lng")
    private BigDecimal serviceLng;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "priority")
    private String priority = "NORMAL";

    @Column(name = "status")
    private String status = "SCHEDULED";

    @Column(name = "work_notes")
    @Lob
    private String workNotes;

    @Column(name = "parts_used")
    private String partsUsed;

    @Column(name = "cost_estimate")
    private BigDecimal costEstimate;

    @Column(name = "actual_cost")
    private BigDecimal actualCost;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ServiceType { INSTALLATION, MAINTENANCE, REPAIR, INSPECTION, DELIVERY, CONSULTATION }
    public enum Priority { LOW, NORMAL, HIGH, URGENT }
    public enum Status { SCHEDULED, DISPATCHED, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED }
}
