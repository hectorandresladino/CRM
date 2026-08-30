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
@Table(name = "integrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Integration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Boolean connected = false;

    @Column(columnDefinition = "TEXT")
    private String credentials;

    @Column(name = "sync_enabled")
    private Boolean syncEnabled = false;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "sync_frequency")
    private String syncFrequency;

    @Column(columnDefinition = "TEXT")
    private String config;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Provider {
        STRIPE, MERCADO_PAGO, GOOGLE_CALENDAR, GOOGLE_WORKSPACE,
        AZURE_AD, OKTA, SLACK, ZAPIER, MAKE, QUICKBOOKS,
        WHATSAPP_BUSINESS, META_BUSINESS, SHOPIFY, ALEGRA, DIAN
    }

    public enum Category {
        PAYMENT, CALENDAR, SSO, COMMUNICATION, AUTOMATION, ACCOUNTING, ECOMMERCE, COMPLIANCE
    }
}
