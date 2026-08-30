/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal priceMonthly;

    @Column(nullable = false)
    private BigDecimal priceYearly;

    @Column(nullable = false)
    private String currency;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "max_clients")
    private Integer maxClients;

    @Column(name = "max_storage_mb")
    private Long maxStorageMb;

    @Column(name = "max_automations")
    private Integer maxAutomations;

    @Column(name = "max_contacts")
    private Integer maxContacts;

    @Column(name = "max_whatsapp_messages")
    private Integer maxWhatsappMessages;

    @Column(name = "max_emails")
    private Integer maxEmails;

    @Column(name = "max_sms")
    private Integer maxSms;

    @Column(name = "max_api_calls")
    private Integer maxApiCalls;

    @Column(name = "max_ai_predictions")
    private Integer maxAiPredictions;

    @Column(name = "max_sub_accounts")
    private Integer maxSubAccounts;

    @Column(name = "is_agency_plan")
    private Boolean isAgencyPlan = false;

    @Column(name = "has_whatsapp")
    private Boolean hasWhatsapp = false;

    @Column(name = "has_email_marketing")
    private Boolean hasEmailMarketing = false;

    @Column(name = "has_api_access")
    private Boolean hasApiAccess = false;

    @Column(name = "has_white_label")
    private Boolean hasWhiteLabel = false;

    @Column(name = "has_ai_features")
    private Boolean hasAiFeatures = false;

    @Column(name = "has_advanced_reports")
    private Boolean hasAdvancedReports = false;

    @Column(name = "has_webhooks")
    private Boolean hasWebhooks = false;

    @Column(name = "trial_days")
    private Integer trialDays = 14;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum PlanType {
        STARTER, BUSINESS, ENTERPRISE, AGENCY
    }
}
