/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_portal_access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientPortalAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(nullable = false, unique = true)
    private String portalToken;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_count")
    private Integer loginCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
