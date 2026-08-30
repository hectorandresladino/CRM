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
@Table(name = "data_exports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "export_format")
    private String exportFormat;

    @Column(name = "filter_criteria", columnDefinition = "TEXT")
    private String filterCriteria;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "requested_by")
    private Long requestedBy;

    @Column(name = "requested_by_name")
    private String requestedByName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExportStatus status = ExportStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ExportStatus { PENDING, RUNNING, COMPLETED, FAILED, EXPIRED }
    public enum ExportFormat { CSV, EXCEL, JSON, XML, PDF }
}
