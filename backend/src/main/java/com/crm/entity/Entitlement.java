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
@Table(name = "entitlements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entitlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(nullable = false)
    private String name;

    @Column(name = "entitlement_type")
    private String entitlementType;

    @Column(name = "sla_process_id")
    private Long slaProcessId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "cases_remaining")
    private Integer casesRemaining;

    @Column(name = "total_cases")
    private Integer totalCases;

    @Column(name = "status")
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum Type { SUPPORT, MAINTENANCE, WARRANTY, SUBSCRIPTION }
    public enum Status { ACTIVE, EXPIRED, SUSPENDED, CANCELLED }
}
