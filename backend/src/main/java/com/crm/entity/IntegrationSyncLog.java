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
@Table(name = "integration_sync_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "integration_id", nullable = false)
    private Long integrationId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "sync_direction")
    private String syncDirection;

    @Column(name = "records_processed")
    private Integer recordsProcessed = 0;

    @Column(name = "records_created")
    private Integer recordsCreated = 0;

    @Column(name = "records_updated")
    private Integer recordsUpdated = 0;

    @Column(name = "records_failed")
    private Integer recordsFailed = 0;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status = SyncStatus.RUNNING;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public enum SyncStatus { RUNNING, COMPLETED, PARTIAL, FAILED, CANCELLED }
    public enum SyncDirection { INBOUND, OUTBOUND, BIDIRECTIONAL }
}
