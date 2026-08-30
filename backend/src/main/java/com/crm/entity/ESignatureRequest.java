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
@Table(name = "esignature_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ESignatureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "contrato_id")
    private Long contratoId;

    @Column(nullable = false)
    private String documentTitle;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "signer_name", nullable = false)
    private String signerName;

    @Column(name = "signer_email", nullable = false)
    private String signerEmail;

    @Column(name = "signer_phone")
    private String signerPhone;

    @Column(nullable = false)
    private String status;

    @Column(name = "signature_token", nullable = false, unique = true)
    private String signatureToken;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "signature_hash")
    private String signatureHash;

    @Column(name = "signer_ip")
    private String signerIp;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(columnDefinition = "TEXT")
    private String auditTrail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING, SIGNED, EXPIRED, CANCELLED
    }
}
