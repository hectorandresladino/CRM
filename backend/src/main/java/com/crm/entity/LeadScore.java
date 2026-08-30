package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "prospecto_id", nullable = false)
    private Long prospectoId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String grade;

    @Column(columnDefinition = "TEXT")
    private String factors;

    @Column(name = "email_engagement")
    private Integer emailEngagement = 0;

    @Column(name = "website_visits")
    private Integer websiteVisits = 0;

    @Column(name = "whatsapp_interactions")
    private Integer whatsappInteractions = 0;

    @Column(name = "response_time_hours")
    private Integer responseTimeHours;

    @Column(name = "company_size")
    private String companySize;

    @Column(name = "budget_indicated")
    private Boolean budgetIndicated;

    @Column(name = "decision_maker")
    private Boolean decisionMaker;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
