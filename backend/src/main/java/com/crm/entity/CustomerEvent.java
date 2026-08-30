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
@Table(name = "customer_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "prospecto_id")
    private Long prospectoId;

    @Column(name = "unified_profile_id")
    private String unifiedProfileId;

    @Column(nullable = false)
    private String eventType;

    @Column(name = "event_source")
    private String eventSource;

    @Column(name = "event_channel")
    private String eventChannel;

    @Column(name = "event_data")
    @Lob
    private String eventData;

    @Column(name = "page_url")
    private String pageUrl;

    @Column(name = "referrer")
    private String referrer;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "location_country")
    private String locationCountry;

    @Column(name = "location_city")
    private String locationCity;

    @Column(name = "event_timestamp")
    private LocalDateTime eventTimestamp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); if (eventTimestamp == null) eventTimestamp = LocalDateTime.now(); }

    public enum EventType { PAGE_VIEW, CLICK, SIGNUP, LOGIN, PURCHASE, ADD_TO_CART, SEARCH, DOWNLOAD, VIDEO_PLAY, FORM_SUBMIT, EMAIL_OPEN, EMAIL_CLICK, WHATSAPP_MESSAGE, SUPPORT_TICKET }
    public enum EventSource { WEB, MOBILE, API, EMAIL, WHATSAPP, SOCIAL, OFFLINE, IMPORT }
    public enum EventChannel { WEBSITE, APP, EMAIL, SMS, WHATSAPP, PHONE, SOCIAL, IN_PERSON }
}
