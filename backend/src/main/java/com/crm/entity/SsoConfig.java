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
@Table(name = "sso_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SsoProvider provider;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "tenant_external_id")
    private String tenantExternalId;

    @Column(name = "metadata_url")
    private String metadataUrl;

    @Column(name = "certificate", columnDefinition = "TEXT")
    private String certificate;

    @Column(name = "is_enabled")
    private Boolean isEnabled = false;

    @Column(name = "auto_provision_users")
    private Boolean autoProvisionUsers = true;

    @Column(name = "default_role_id")
    private Long defaultRoleId;

    @Column(name = "last_tested_at")
    private LocalDateTime lastTestedAt;

    @Column(name = "last_test_result")
    private String lastTestResult;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum SsoProvider { SAML, OAUTH2, OIDC, AZURE_AD, GOOGLE_WORKSPACE, OKTA, AUTH0, KEYCLOAK }
}
