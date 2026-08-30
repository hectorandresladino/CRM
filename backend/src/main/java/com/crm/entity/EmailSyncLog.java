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
@Table(name = "email_sync_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailSyncLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailProvider provider;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "thread_id")
    private String threadId;

    @Column(nullable = false)
    private String subject;

    @Column(name = "sender_email")
    private String senderEmail;

    @Column(name = "recipient_emails", columnDefinition = "TEXT")
    private String recipientEmails;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(name = "is_incoming")
    private Boolean isIncoming;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "is_replied")
    private Boolean isReplied = false;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "opportunity_id")
    private Long opportunityId;

    @Column(name = "synced_at", updatable = false)
    private LocalDateTime syncedAt;

    @PrePersist
    protected void onCreate() {
        syncedAt = LocalDateTime.now();
    }

    public enum EmailProvider {
        GMAIL, OUTLOOK, IMAP, OTHER
    }
}
