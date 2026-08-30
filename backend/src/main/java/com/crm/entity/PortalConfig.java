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
@Table(name = "portal_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String name;

    @Column(name = "portal_type")
    private String portalType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "custom_domain")
    private String customDomain;

    @Column(name = "theme_primary_color")
    private String themePrimaryColor;

    @Column(name = "theme_secondary_color")
    private String themeSecondaryColor;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "header_html")
    @Lob
    private String headerHtml;

    @Column(name = "footer_html")
    @Lob
    private String footerHtml;

    @Column(name = "visible_objects")
    @Lob
    private String visibleObjects;

    @Column(name = "self_service_actions")
    @Lob
    private String selfServiceActions;

    @Column(name = "require_login")
    private Boolean requireLogin = true;

    @Column(name = "allow_registration")
    private Boolean allowRegistration = true;

    @Column(name = "default_language")
    private String defaultLanguage = "es";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum PortalType { CUSTOMER, PARTNER, COMMUNITY }
}
