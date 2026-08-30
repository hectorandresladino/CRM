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
@Table(name = "territories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Territory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "geo_region")
    private String geoRegion;

    @Column(name = "countries")
    private String countries;

    @Column(name = "states")
    private String states;

    @Column(name = "cities")
    private String cities;

    @Column(name = "zip_codes")
    private String zipCodes;

    @Column(name = "target_revenue")
    private java.math.BigDecimal targetRevenue;

    @Column(name = "account_count")
    private Integer accountCount;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
