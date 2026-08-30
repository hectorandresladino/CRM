/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.AIPrediction;
import com.crm.entity.Cliente;
import com.crm.entity.Venta;
import com.crm.repository.AIPredictionRepository;
import com.crm.repository.ClienteRepository;
import com.crm.repository.VentaRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Explainable deterministic baselines using only tenant-owned CRM data. */
@Service
@RequiredArgsConstructor
@Transactional
public class RevenueAIService {

    private static final String MODEL_VERSION = "baseline-1.0";
    private final AIPredictionRepository predictionRepo;
    private final ClienteRepository clienteRepo;
    private final VentaRepository ventaRepo;

    public AIPrediction predictOpportunityScore(Long opportunityId) {
        Long tenantId = tenantId();
        Venta opportunity = ventaRepo.findByIdAndTenantId(opportunityId, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Oportunidad"));
        List<Venta> portfolio = ventaRepo.findByTenantId(tenantId);
        double stageScore = switch (opportunity.getEstado()) {
            case CERRADA -> 100;
            case EN_PROCESO -> 65;
            case PENDIENTE -> 38;
            case CANCELADA -> 0;
        };
        double average = portfolio.stream().map(this::amount).mapToDouble(BigDecimal::doubleValue)
                .average().orElse(amount(opportunity).doubleValue());
        double valueSignal = average > 0 ? Math.min(15, amount(opportunity).doubleValue() / average * 7.5) : 7.5;
        long ageDays = opportunity.getFechaCreacion() == null ? 0
                : ChronoUnit.DAYS.between(opportunity.getFechaCreacion(), LocalDateTime.now());
        double score = clamp(stageScore * 0.75 + valueSignal + Math.max(-20, 10 - ageDays / 6.0), 0, 100);

        AIPrediction prediction = base("OPPORTUNITY_SCORING", "VENTA", opportunityId);
        prediction.setPredictedValue(decimal(score));
        prediction.setProbability(score / 100.0);
        prediction.setConfidenceScore(sampleConfidence(portfolio.size()));
        prediction.setModelName("revenue-opportunity-baseline");
        prediction.setFeaturesUsed("[\"stage\",\"amountVsTenantAverage\",\"daysOpen\"]");
        prediction.setExplanation("Cálculo determinístico con etapa, valor relativo y antigüedad de la oportunidad.");
        prediction.setRecommendedAction(score >= 80 ? "PRIORITIZE_CLOSE" : score >= 55 ? "FOLLOW_UP" : "REQUALIFY");
        return predictionRepo.save(prediction);
    }

    public AIPrediction predictChurn(Long clientId) {
        Long tenantId = tenantId();
        clienteRepo.findByIdAndTenantId(clientId, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        List<Venta> sales = ventaRepo.findByTenantIdAndClienteId(tenantId, clientId);
        LocalDateTime lastActivity = sales.stream().map(Venta::getFechaCreacion).filter(v -> v != null)
                .max(LocalDateTime::compareTo).orElse(null);
        long inactiveDays = lastActivity == null ? 365 : ChronoUnit.DAYS.between(lastActivity, LocalDateTime.now());
        long cancelled = sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.CANCELADA).count();
        double cancellationRate = sales.isEmpty() ? 0 : (double) cancelled / sales.size();
        double risk = clamp((inactiveDays / 180.0) * 65 + cancellationRate * 35, 5, 95);

        AIPrediction prediction = base("CHURN_PREDICTION", "CLIENTE", clientId);
        prediction.setPredictedValue(decimal(risk));
        prediction.setProbability(risk / 100.0);
        prediction.setConfidenceScore(sampleConfidence(sales.size()));
        prediction.setModelName("customer-retention-baseline");
        prediction.setFeaturesUsed("[\"daysSinceLastSale\",\"cancelledSaleRate\",\"saleCount\"]");
        prediction.setExplanation("Riesgo calculado con recencia y cancelaciones reales del cliente.");
        prediction.setRecommendedAction(risk >= 70 ? "RETENTION_OUTREACH" : risk >= 40 ? "CHECK_IN" : "MONITOR");
        return predictionRepo.save(prediction);
    }

    public AIPrediction nextBestAction(Long clientId) {
        Long tenantId = tenantId();
        Cliente client = clienteRepo.findByIdAndTenantId(clientId, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        List<Venta> sales = ventaRepo.findByTenantIdAndClienteId(tenantId, clientId);
        BigDecimal closedRevenue = sales.stream().filter(v -> v.getEstado() == Venta.EstadoVenta.CERRADA)
                .map(this::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime lastSale = sales.stream().map(Venta::getFechaCreacion).filter(v -> v != null)
                .max(LocalDateTime::compareTo).orElse(null);
        long inactiveDays = lastSale == null ? Long.MAX_VALUE : ChronoUnit.DAYS.between(lastSale, LocalDateTime.now());

        String action;
        String reason;
        if (sales.isEmpty()) {
            action = "SCHEDULE_DISCOVERY";
            reason = "El cliente todavía no registra oportunidades; conviene descubrir necesidades.";
        } else if (inactiveDays > 90) {
            action = "REACTIVATION_SEQUENCE";
            reason = "La última actividad comercial supera 90 días.";
        } else if (closedRevenue.signum() > 0) {
            action = "UPSELL_REVIEW";
            reason = "El historial de compras permite revisar expansión o renovación.";
        } else {
            action = "PROPOSAL_FOLLOW_UP";
            reason = "Existen oportunidades, pero aún no hay ingresos cerrados.";
        }

        AIPrediction prediction = base("NEXT_BEST_ACTION", "CLIENTE", clientId);
        prediction.setRecommendedAction(action);
        prediction.setProbability(Math.min(0.9, 0.55 + sales.size() * 0.04));
        prediction.setConfidenceScore(sampleConfidence(sales.size()));
        prediction.setModelName("next-action-rules");
        prediction.setFeaturesUsed("[\"saleCount\",\"closedRevenue\",\"daysSinceLastSale\",\"clientStatus\"]");
        prediction.setExplanation(reason + " Estado actual: " + client.getEstado() + ".");
        return predictionRepo.save(prediction);
    }

    public AIPrediction forecastIA(Integer year, Integer quarter) {
        if (quarter == null || quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("El trimestre debe estar entre 1 y 4");
        }
        int targetYear = year != null ? year : LocalDate.now().getYear();
        LocalDateTime start = LocalDate.of(targetYear, (quarter - 1) * 3 + 1, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(3);
        List<Venta> period = ventaRepo.findByTenantId(tenantId()).stream()
                .filter(v -> v.getFechaCreacion() != null && !v.getFechaCreacion().isBefore(start)
                        && v.getFechaCreacion().isBefore(end)).toList();
        BigDecimal forecast = period.stream().map(v -> amount(v).multiply(stageWeight(v.getEstado())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        AIPrediction prediction = base("FORECAST_IA", null, null);
        prediction.setPredictedValue(forecast.setScale(2, RoundingMode.HALF_UP));
        prediction.setConfidenceScore(sampleConfidence(period.size()));
        prediction.setModelName("weighted-pipeline-forecast");
        prediction.setFeaturesUsed("[\"periodPipeline\",\"stageProbability\",\"dealValue\"]");
        prediction.setExplanation("Forecast Q" + quarter + " " + targetYear + " calculado con valores reales ponderados por etapa.");
        prediction.setRecommendedAction("REVIEW_PIPELINE_COVERAGE");
        return predictionRepo.save(prediction);
    }

    public AIPrediction lifetimeValue(Long clientId) {
        Long tenantId = tenantId();
        Cliente client = clienteRepo.findByIdAndTenantId(clientId, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Cliente"));
        List<Venta> closed = ventaRepo.findByTenantIdAndClienteId(tenantId, clientId).stream()
                .filter(v -> v.getEstado() == Venta.EstadoVenta.CERRADA).toList();
        BigDecimal revenue = closed.stream().map(this::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long tenureDays = client.getFechaCreacion() == null ? 365
                : Math.max(30, ChronoUnit.DAYS.between(client.getFechaCreacion(), LocalDateTime.now()));
        double annualFrequency = closed.size() * 365.0 / tenureDays;
        BigDecimal averageOrder = closed.isEmpty() ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(closed.size()), 2, RoundingMode.HALF_UP);
        BigDecimal projectedTwoYears = averageOrder.multiply(BigDecimal.valueOf(annualFrequency * 2));

        AIPrediction prediction = base("LIFETIME_VALUE", "CLIENTE", clientId);
        prediction.setPredictedValue(projectedTwoYears.setScale(2, RoundingMode.HALF_UP));
        prediction.setConfidenceScore(sampleConfidence(closed.size()));
        prediction.setModelName("customer-value-baseline");
        prediction.setFeaturesUsed("[\"closedRevenue\",\"averageOrderValue\",\"annualPurchaseFrequency\",\"tenureDays\"]");
        prediction.setExplanation("Proyección a 24 meses basada en frecuencia y ticket promedio históricos.");
        return predictionRepo.save(prediction);
    }

    public List<AIPrediction> getPredictions(String type) {
        Long tenantId = tenantId();
        return type != null ? predictionRepo.findByTenantIdAndPredictionType(tenantId, type)
                : predictionRepo.findByTenantId(tenantId);
    }

    public AIPrediction actionPrediction(Long id) {
        AIPrediction prediction = predictionRepo.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Predicción"));
        prediction.setIsActioned(true);
        prediction.setActionedAt(LocalDateTime.now());
        return predictionRepo.save(prediction);
    }

    public Map<String, Object> getAIInsights() {
        List<AIPrediction> all = predictionRepo.findByTenantId(tenantId());
        Map<String, Object> insights = new HashMap<>();
        insights.put("calculationType", "REAL_DATA_BASELINE");
        insights.put("totalPredictions", all.size());
        insights.put("actionedPredictions", all.stream().filter(p -> Boolean.TRUE.equals(p.getIsActioned())).count());
        insights.put("pendingActions", all.stream().filter(p -> !Boolean.TRUE.equals(p.getIsActioned())).count());
        insights.put("highConfidencePredictions", all.stream()
                .filter(p -> p.getConfidenceScore() != null && p.getConfidenceScore() > 0.8).count());
        insights.put("predictionsByType", all.stream().collect(java.util.stream.Collectors.groupingBy(
                AIPrediction::getPredictionType, java.util.stream.Collectors.counting())));
        return insights;
    }

    private AIPrediction base(String type, String entity, Long targetId) {
        AIPrediction prediction = new AIPrediction();
        prediction.setTenantId(tenantId());
        prediction.setPredictionType(type);
        prediction.setTargetEntity(entity);
        prediction.setTargetId(targetId);
        prediction.setModelVersion(MODEL_VERSION);
        return prediction;
    }

    private BigDecimal amount(Venta sale) {
        return sale.getTotal() != null ? sale.getTotal() : sale.getMonto() != null ? sale.getMonto() : BigDecimal.ZERO;
    }

    private BigDecimal stageWeight(Venta.EstadoVenta status) {
        return switch (status) {
            case CERRADA -> BigDecimal.ONE;
            case EN_PROCESO -> BigDecimal.valueOf(0.65);
            case PENDIENTE -> BigDecimal.valueOf(0.30);
            case CANCELADA -> BigDecimal.ZERO;
        };
    }

    private double sampleConfidence(int sampleSize) {
        return Math.min(0.90, 0.35 + Math.log10(sampleSize + 1) * 0.25);
    }

    private BigDecimal decimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
