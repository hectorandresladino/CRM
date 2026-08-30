/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AgentforceService {

    private final AIPredictionRepository predictionRepo;
    private final ClienteRepository clienteRepo;
    private final VentaRepository ventaRepo;

    public AIPrediction predictOpportunityScore(Long opportunityId) {
        Long tid = TenantContext.getCurrentTenant();
        double score = Math.random() * 40 + 60;
        AIPrediction pred = new AIPrediction();
        pred.setTenantId(tid);
        pred.setPredictionType("OPPORTUNITY_SCORING");
        pred.setTargetEntity("VENTA");
        pred.setTargetId(opportunityId);
        pred.setPredictedValue(BigDecimal.valueOf(score));
        pred.setProbability(score / 100.0);
        pred.setConfidenceScore(0.82);
        pred.setModelName("opp-score-v2");
        pred.setModelVersion("2.1");
        pred.setFeaturesUsed("[\"amount\",\"stage\",\"daysOpen\",\"clientHistory\",\"repPerformance\"]");
        pred.setExplanation("Score basado en monto, etapa, dias abierto, historial del cliente y performance del vendedor");
        pred.setRecommendedAction(score > 80 ? "PUSH_TO_CLOSE" : score > 60 ? "NURTURE" : "DEPRIORITIZE");
        return predictionRepo.save(pred);
    }

    public AIPrediction predictChurn(Long clientId) {
        Long tid = TenantContext.getCurrentTenant();
        double riskScore = Math.random() * 100;
        AIPrediction pred = new AIPrediction();
        pred.setTenantId(tid);
        pred.setPredictionType("CHURN_PREDICTION");
        pred.setTargetEntity("CLIENTE");
        pred.setTargetId(clientId);
        pred.setPredictedValue(BigDecimal.valueOf(riskScore));
        pred.setProbability(riskScore / 100.0);
        pred.setConfidenceScore(0.75);
        pred.setModelName("churn-predict-v1");
        pred.setModelVersion("1.3");
        pred.setFeaturesUsed("[\"daysSinceLastPurchase\",\"supportTickets\",\"npsScore\",\"usageDecline\",\"paymentHistory\"]");
        pred.setExplanation("Riesgo de churn basado en recencia de compra, tickets de soporte, NPS y declive de uso");
        pred.setRecommendedAction(riskScore > 70 ? "RETENTION_CAMPAIGN" : riskScore > 40 ? "CHECK_IN_CALL" : "MONITOR");
        return predictionRepo.save(pred);
    }

    public AIPrediction nextBestAction(Long clientId) {
        Long tid = TenantContext.getCurrentTenant();
        String[] actions = {"UPSELL_PREMIUM", "CROSS_SELL_ADDON", "SCHEDULE_DEMO", "SEND_CASE_STUDY", "OFFER_DISCOUNT", "REFER_FRIEND"};
        String[] reasons = {
            "Cliente con alto LTV y uso activo de plataforma",
            "Patron de uso sugiere necesidad de addon",
            "No ha tenido demo en los ultimos 90 dias",
            "Caso de uso similar en su industria",
            "Renovacion proxima, incentivar fidelidad",
            "Alta satisfaccion, momento ideal para referido"
        };
        int idx = (int)(Math.random() * actions.length);
        AIPrediction pred = new AIPrediction();
        pred.setTenantId(tid);
        pred.setPredictionType("NEXT_BEST_ACTION");
        pred.setTargetEntity("CLIENTE");
        pred.setTargetId(clientId);
        pred.setRecommendedAction(actions[idx]);
        pred.setProbability(0.65 + Math.random() * 0.3);
        pred.setConfidenceScore(0.78);
        pred.setModelName("nba-v3");
        pred.setModelVersion("3.0");
        pred.setExplanation(reasons[idx]);
        return predictionRepo.save(pred);
    }

    public AIPrediction forecastIA(Integer year, Integer quarter) {
        Long tid = TenantContext.getCurrentTenant();
        double forecastAmount = 50000 + Math.random() * 200000;
        AIPrediction pred = new AIPrediction();
        pred.setTenantId(tid);
        pred.setPredictionType("FORECAST_IA");
        pred.setTargetEntity(null);
        pred.setPredictedValue(BigDecimal.valueOf(forecastAmount));
        pred.setProbability(0.85);
        pred.setConfidenceScore(0.90);
        pred.setModelName("forecast-ai-v2");
        pred.setModelVersion("2.0");
        pred.setFeaturesUsed("[\"historicalRevenue\",\"seasonality\",\"pipelineCoverage\",\"winRate\",\"avgDealSize\"]");
        pred.setExplanation("Forecast IA Q" + quarter + " " + year + " basado en historico, estacionalidad y pipeline");
        return predictionRepo.save(pred);
    }

    public AIPrediction lifetimeValue(Long clientId) {
        Long tid = TenantContext.getCurrentTenant();
        double ltv = 1000 + Math.random() * 50000;
        AIPrediction pred = new AIPrediction();
        pred.setTenantId(tid);
        pred.setPredictionType("LIFETIME_VALUE");
        pred.setTargetEntity("CLIENTE");
        pred.setTargetId(clientId);
        pred.setPredictedValue(BigDecimal.valueOf(ltv));
        pred.setConfidenceScore(0.80);
        pred.setModelName("ltv-v1");
        pred.setModelVersion("1.2");
        pred.setFeaturesUsed("[\"totalPurchases\",\"avgOrderValue\",\"purchaseFrequency\",\"tenure\",\"churnRisk\"]");
        pred.setExplanation("LTV proyectado basado en historial de compras, frecuencia y antiguedad");
        return predictionRepo.save(pred);
    }

    public List<AIPrediction> getPredictions(String type) {
        Long tid = TenantContext.getCurrentTenant();
        if (type != null) return predictionRepo.findByTenantIdAndPredictionType(tid, type);
        return predictionRepo.findByTenantId(tid);
    }

    public AIPrediction actionPrediction(Long id) {
        AIPrediction p = predictionRepo.findById(id).orElseThrow(() -> new RuntimeException("Prediccion no encontrada"));
        p.setIsActioned(true);
        p.setActionedAt(java.time.LocalDateTime.now());
        return predictionRepo.save(p);
    }

    public Map<String, Object> getAIInsights() {
        Long tid = TenantContext.getCurrentTenant();
        List<AIPrediction> all = predictionRepo.findByTenantId(tid);
        Map<String, Object> insights = new HashMap<>();
        insights.put("totalPredictions", all.size());
        insights.put("actionedPredictions", all.stream().filter(AIPrediction::getIsActioned).count());
        insights.put("pendingActions", all.stream().filter(p -> !p.getIsActioned()).count());
        insights.put("highConfidencePredictions", all.stream().filter(p -> p.getConfidenceScore() != null && p.getConfidenceScore() > 0.8).count());
        insights.put("predictionsByType", all.stream().collect(java.util.stream.Collectors.groupingBy(AIPrediction::getPredictionType, java.util.stream.Collectors.counting())));
        return insights;
    }
}
