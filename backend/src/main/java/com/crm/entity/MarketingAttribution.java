/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketing_attribution")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketingAttribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "touchpoint_type")
    private String touchpointType;

    @Column(name = "touchpoint_channel")
    private String touchpointChannel;

    @Column(name = "touchpoint_value")
    private BigDecimal touchpointValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttributionModel model;

    @Column(name = "attribution_weight")
    private BigDecimal attributionWeight;

    @Column(name = "revenue_attributed")
    private BigDecimal revenueAttributed;

    @Column(name = "touchpoint_date")
    private LocalDateTime touchpointDate;

    @Column(name = "conversion_date")
    private LocalDateTime conversionDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum AttributionModel { FIRST_TOUCH, LAST_TOUCH, LINEAR, TIME_DECAY, U_SHAPED, W_SHAPED, CUSTOM }
}
