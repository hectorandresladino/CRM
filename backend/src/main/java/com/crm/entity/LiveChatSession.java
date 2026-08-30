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
@Table(name = "live_chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "visitor_name")
    private String visitorName;

    @Column(name = "visitor_email")
    private String visitorEmail;

    @Column(name = "visitor_ip")
    private String visitorIp;

    @Column(name = "visitor_country")
    private String visitorCountry;

    @Column(name = "visitor_city")
    private String visitorCity;

    @Column(name = "page_url")
    private String pageUrl;

    @Column(name = "assigned_agent_id")
    private Long assignedAgentId;

    @Column(name = "assigned_agent_name")
    private String assignedAgentName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.WAITING;

    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "wait_time_seconds")
    private Integer waitTimeSeconds;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @PrePersist
    protected void onCreate() { startedAt = LocalDateTime.now(); }

    public enum ChatStatus { WAITING, ACTIVE, TRANSFERRED, ENDED, ABANDONED }
}
