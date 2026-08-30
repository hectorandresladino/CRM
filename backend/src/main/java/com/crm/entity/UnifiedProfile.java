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
@Table(name = "unified_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "profile_uuid", nullable = false, unique = true)
    private String profileUuid;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "prospecto_id")
    private Long prospectoId;

    @Column(name = "primary_email")
    private String primaryEmail;

    @Column(name = "primary_phone")
    private String primaryPhone;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "company")
    private String company;

    @Column(name = "identity_sources")
    private String identitySources;

    @Column(name = "match_confidence")
    private Double matchConfidence;

    @Column(name = "total_events")
    private Integer totalEvents = 0;

    @Column(name = "last_event_at")
    private LocalDateTime lastEventAt;

    @Column(name = "lifecycle_stage")
    private String lifecycleStage = "UNKNOWN";

    @Column(name = "segments")
    private String segments;

    @Column(name = "attributes")
    @Lob
    private String attributes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum LifecycleStage { UNKNOWN, VISITOR, LEAD, MQL, SQL, OPPORTUNITY, CUSTOMER, ACTIVE, CHURNED, REACTIVATED }
}
