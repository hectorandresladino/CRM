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
@Table(name = "account_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "team_role", nullable = false)
    private String teamRole;

    @Column(name = "access_level")
    private String accessLevel = "READ";

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() { assignedAt = LocalDateTime.now(); }

    public enum TeamRole { ACCOUNT_OWNER, SALES_REP, SALES_ENGINEER, ACCOUNT_MANAGER, CUSTOMER_SUCCESS, EXECUTIVE_SPONSOR }
    public enum AccessLevel { READ, EDIT, FULL }
}
