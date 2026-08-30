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
@Table(name = "report_executions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "report_id", nullable = false)
    private Long reportId;

    @Column(name = "report_name")
    private String reportName;

    @Column(name = "executed_by")
    private Long executedBy;

    @Column(name = "executed_by_name")
    private String executedByName;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "format")
    private String format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status = ExecutionStatus.RUNNING;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public enum ExecutionStatus { RUNNING, COMPLETED, FAILED, CANCELLED }
}
