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
@Table(name = "whatsapp_ai_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppAIConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "auto_reply")
    private Boolean autoReply = true;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "welcome_message", columnDefinition = "TEXT")
    private String welcomeMessage;

    @Column(name = "fallback_message", columnDefinition = "TEXT")
    private String fallbackMessage;

    @Column(name = "hours_start")
    private String hoursStart = "08:00";

    @Column(name = "hours_end")
    private String hoursEnd = "18:00";

    @Column(name = "out_of_hours_message", columnDefinition = "TEXT")
    private String outOfHoursMessage;

    @Column(name = "qualify_leads")
    private Boolean qualifyLeads = true;

    @Column(name = "transcribe_audio")
    private Boolean transcribeAudio = true;

    @Column(name = "language")
    private String language = "es";

    @Column(name = "personality")
    private String personality = "professional";

    @Column(name = "system_prompt", columnDefinition = "TEXT")
    private String systemPrompt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
