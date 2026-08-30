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
@Table(name = "password_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "min_length")
    private Integer minLength = 8;

    @Column(name = "require_uppercase")
    private Boolean requireUppercase = true;

    @Column(name = "require_lowercase")
    private Boolean requireLowercase = true;

    @Column(name = "require_numbers")
    private Boolean requireNumbers = true;

    @Column(name = "require_special_chars")
    private Boolean requireSpecialChars = true;

    @Column(name = "special_chars")
    private String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    @Column(name = "password_expiry_days")
    private Integer passwordExpiryDays = 90;

    @Column(name = "password_history_count")
    private Integer passwordHistoryCount = 5;

    @Column(name = "max_login_attempts")
    private Integer maxLoginAttempts = 5;

    @Column(name = "lockout_duration_minutes")
    private Integer lockoutDurationMinutes = 30;

    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 60;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
