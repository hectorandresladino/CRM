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
@Table(name = "push_notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "notification_type")
    private String notificationType;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "data_payload", columnDefinition = "TEXT")
    private String dataPayload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(name = "provider_message_id")
    private String providerMessageId;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() { sentAt = LocalDateTime.now(); }

    public enum DeliveryStatus { PENDING, SENT, DELIVERED, FAILED, EXPIRED }
}
