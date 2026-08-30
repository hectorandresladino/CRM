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
@Table(name = "compliance_audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStandard standard;

    @Column(name = "audit_type")
    private String auditType;

    @Column(name = "auditor")
    private String auditor;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditResult result = AuditResult.PENDING;

    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "score")
    private Double score;

    @Column(name = "report_url")
    private String reportUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ComplianceStandard { GDPR, CCPA, SOX, HIPAA, PCI_DSS, ISO_27001, SOC2, NIST }
    public enum AuditResult { PENDING, PASSED, PASSED_WITH_FINDINGS, FAILED, IN_PROGRESS }
}
