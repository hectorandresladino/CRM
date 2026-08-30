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
@Table(name = "rate_limit_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "requests_per_minute")
    private Integer requestsPerMinute = 100;

    @Column(name = "requests_per_hour")
    private Integer requestsPerHour = 1000;

    @Column(name = "requests_per_day")
    private Integer requestsPerDay = 10000;

    @Column(name = "burst_limit")
    private Integer burstLimit = 10;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "current_requests_minute")
    private Integer currentRequestsMinute = 0;

    @Column(name = "current_requests_hour")
    private Integer currentRequestsHour = 0;

    @Column(name = "current_requests_day")
    private Integer currentRequestsDay = 0;

    @Column(name = "last_reset_at")
    private LocalDateTime lastResetAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); lastResetAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
