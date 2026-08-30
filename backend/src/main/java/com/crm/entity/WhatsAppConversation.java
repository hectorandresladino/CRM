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
@Table(name = "whatsapp_conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "prospecto_id")
    private Long prospectoId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false)
    private String direction;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "ai_response")
    private Boolean aiResponse = false;

    @Column(name = "ai_intent")
    private String aiIntent;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "ai_handled")
    private Boolean aiHandled = true;

    @Column(name = "human_taken_over")
    private Boolean humanTakenOver = false;

    @Column(name = "assigned_agent")
    private String assignedAgent;

    @Column(name = "sentiment")
    private String sentiment;

    @Enumerated(EnumType.STRING)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ConversationStatus {
        ACTIVE, WAITING_AGENT, RESOLVED, ARCHIVED
    }

    public enum Direction {
        INBOUND, OUTBOUND
    }
}
