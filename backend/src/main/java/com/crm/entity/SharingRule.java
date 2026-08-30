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
@Table(name = "sharing_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SharingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "share_with_type")
    private String shareWithType;

    @Column(name = "share_with_id")
    private Long shareWithId;

    @Column(name = "criteria_field")
    private String criteriaField;

    @Column(name = "criteria_operator")
    private String criteriaOperator;

    @Column(name = "criteria_value")
    private String criteriaValue;

    @Column(name = "access_level")
    private String accessLevel = "READ";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum RuleType { CRITERIA_BASED, OWNER_BASED, TERRITORY_BASED }
    public enum ShareWithType { ROLE, ROLE_AND_SUBORDINATES, PUBLIC_GROUP, TERRITORY, SPECIFIC_USER }
    public enum AccessLevel { READ, EDIT, FULL }
}
