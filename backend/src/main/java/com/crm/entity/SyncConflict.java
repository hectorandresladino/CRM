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
@Table(name = "sync_conflicts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncConflict {

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

    @Column(name = "server_version", columnDefinition = "TEXT")
    private String serverVersion;

    @Column(name = "client_version", columnDefinition = "TEXT")
    private String clientVersion;

    @Column(name = "server_updated_at")
    private LocalDateTime serverUpdatedAt;

    @Column(name = "client_updated_at")
    private LocalDateTime clientUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResolutionStrategy resolution = ResolutionStrategy.PENDING;

    @Column(name = "resolved_data", columnDefinition = "TEXT")
    private String resolvedData;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ResolutionStrategy { PENDING, SERVER_WINS, CLIENT_WINS, MERGE, MANUAL }
}
