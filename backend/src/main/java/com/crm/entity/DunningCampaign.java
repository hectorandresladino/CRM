package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "dunning_campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DunningCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "step_number")
    private Integer stepNumber;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "message_template")
    private String messageTemplate;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "response_received")
    private Boolean responseReceived = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum ActionType { EMAIL_REMINDER, SMS_REMINDER, PHONE_CALL, SUSPEND_SERVICE, FINAL_NOTICE, COLLECTION_AGENCY }
    public enum Status { PENDING, SENT, RESPONDED, ESCALATED, RESOLVED }
}
