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
@Table(name = "mobile_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "device_uuid")
    private String deviceUuid;

    @Column(name = "is_registered")
    private Boolean isRegistered = true;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() { registeredAt = LocalDateTime.now(); lastSeenAt = LocalDateTime.now(); }

    public enum OsType { ANDROID, IOS, WEB }
}
