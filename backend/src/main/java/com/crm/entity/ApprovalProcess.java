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
@Table(name = "approval_processes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "description")
    private String description;

    @Column(name = "entry_criteria")
    @Lob
    private String entryCriteria;

    @Column(name = "approval_steps")
    @Lob
    private String approvalSteps;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "allow_recall")
    private Boolean allowRecall = true;

    @Column(name = "approved_action")
    private String approvedAction;

    @Column(name = "rejected_action")
    private String rejectedAction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
