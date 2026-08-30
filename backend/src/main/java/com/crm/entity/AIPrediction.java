package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "prediction_type", nullable = false)
    private String predictionType;

    @Column(name = "target_entity")
    private String targetEntity;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "predicted_value")
    private BigDecimal predictedValue;

    @Column(name = "probability")
    private Double probability;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "features_used")
    @Lob
    private String featuresUsed;

    @Column(name = "explanation")
    @Lob
    private String explanation;

    @Column(name = "recommended_action")
    private String recommendedAction;

    @Column(name = "is_actioned")
    private Boolean isActioned = false;

    @Column(name = "actioned_at")
    private LocalDateTime actionedAt;

    @Column(name = "predicted_at")
    private LocalDateTime predictedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); predictedAt = LocalDateTime.now(); }

    public enum PredictionType { OPPORTUNITY_SCORING, LEAD_SCORING, CHURN_PREDICTION, NEXT_BEST_ACTION, FORECAST_IA, CROSS_SELL, UPSELL, SENTIMENT, LIFETIME_VALUE }
}
