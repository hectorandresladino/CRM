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
@Table(name = "sso_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSOConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String protocol;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "tenant_uuid")
    private String tenantUuid;

    @Column(name = "redirect_uri")
    private String redirectUri;

    @Column(name = "metadata_url")
    private String metadataUrl;

    @Column(name = "idp_entity_id")
    private String idpEntityId;

    @Column(name = "idp_sso_url")
    private String idpSsoUrl;

    @Column(name = "idp_certificate")
    private String idpCertificate;

    @Column(name = "sp_entity_id")
    private String spEntityId;

    @Column(name = "attribute_mapping")
    private String attributeMapping;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "auto_provision")
    private Boolean autoProvision = true;

    @Column(name = "default_role")
    private String defaultRole = "VENDEDOR";

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Provider {
        AZURE_AD, GOOGLE_WORKSPACE, OKTA, AUTH0, ONELOGIN, CUSTOM
    }

    public enum Protocol {
        SAML, OAUTH2, OIDC
    }
}
