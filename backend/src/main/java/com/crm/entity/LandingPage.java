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
@Table(name = "landing_pages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandingPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "css_styles", columnDefinition = "TEXT")
    private String cssStyles;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "form_config", columnDefinition = "TEXT")
    private String formConfig;

    @Column(name = "thank_you_url")
    private String thankYouUrl;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "total_visits")
    private Integer totalVisits = 0;

    @Column(name = "total_conversions")
    private Integer totalConversions = 0;

    @Column(name = "conversion_rate")
    private Double conversionRate = 0.0;

    @Column(name = "ab_test_variant")
    private String abTestVariant;

    @Column(name = "ab_test_parent_id")
    private Long abTestParentId;

    @Column(name = "is_published")
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
