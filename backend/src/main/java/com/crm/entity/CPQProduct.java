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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cpq_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CPQProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String currency;

    @Column(name = "min_discount_pct")
    private BigDecimal minDiscountPct;

    @Column(name = "max_discount_pct")
    private BigDecimal maxDiscountPct;

    @Column(name = "cost_price", precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "min_margin_pct")
    private BigDecimal minMarginPct;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "category")
    private String category;

    @Column(name = "unit")
    private String unit = "unit";

    @Column(name = "stock")
    private Integer stock;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
