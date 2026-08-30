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
@Table(name = "offline_sync_queues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfflineSyncQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "operation")
    private String operation;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncStatus status = SyncStatus.PENDING;

    @Column(name = "attempts")
    private Integer attempts = 0;

    @Column(name = "max_attempts")
    private Integer maxAttempts = 5;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "queued_at", updatable = false)
    private LocalDateTime queuedAt;

    @Column(name = "synced_at")
    private LocalDateTime syncedAt;

    @PrePersist
    protected void onCreate() { queuedAt = LocalDateTime.now(); }

    public enum SyncStatus { PENDING, SYNCING, COMPLETED, FAILED, CONFLICT }
    public enum Operation { CREATE, UPDATE, DELETE }
}
