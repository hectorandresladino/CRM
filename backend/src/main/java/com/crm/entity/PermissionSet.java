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
@Table(name = "permission_sets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "object_permissions", columnDefinition = "TEXT")
    private String objectPermissions;

    @Column(name = "field_permissions", columnDefinition = "TEXT")
    private String fieldPermissions;

    @Column(name = "tab_permissions", columnDefinition = "TEXT")
    private String tabPermissions;

    @Column(name = "is_custom")
    private Boolean isCustom = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
