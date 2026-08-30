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
@Table(name = "validation_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "object_name", nullable = false)
    private String objectName;

    @Column(name = "field_name")
    private String fieldName;

    @Column(nullable = false)
    @Lob
    private String formula;

    @Column(nullable = false)
    private String errorMessage;

    @Column(name = "error_location")
    private String errorLocation = "FIELD";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "priority")
    private Integer priority = 1;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ErrorLocation { FIELD, TOP_OF_PAGE }
}
