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
@Table(name = "security_scans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScanStatus status = ScanStatus.PENDING;

    @Column(name = "vulnerabilities_found")
    private Integer vulnerabilitiesFound = 0;

    @Column(name = "critical_count")
    private Integer criticalCount = 0;

    @Column(name = "high_count")
    private Integer highCount = 0;

    @Column(name = "medium_count")
    private Integer mediumCount = 0;

    @Column(name = "low_count")
    private Integer lowCount = 0;

    @Column(name = "scan_results", columnDefinition = "TEXT")
    private String scanResults;

    @Column(name = "report_url")
    private String reportUrl;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ScanType { VULNERABILITY, PENETRATION, CODE_ANALYSIS, DEPENDENCY, CONFIGURATION, CLOUD_SECURITY }
    public enum ScanStatus { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED }
}
