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
@Table(name = "gdpr_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GdprRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_name")
    private String clientName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "deadline_at")
    private LocalDateTime deadlineAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); requestedAt = LocalDateTime.now(); deadlineAt = LocalDateTime.now().plusDays(30); }

    public enum RequestType { ACCESS, RECTIFICATION, ERASURE, PORTABILITY, RESTRICTION, OBJECTION }
    public enum RequestStatus { PENDING, IN_REVIEW, PROCESSING, COMPLETED, REJECTED, CANCELLED }
}
