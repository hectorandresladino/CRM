/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.billing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class StripeProvider implements PaymentProvider {

    @Value("${billing.stripe.api-key:}")
    private String apiKey;

    @Value("${billing.stripe.webhook-secret:}")
    private String webhookSecret;

    @Override
    public String getProviderName() { return "STRIPE"; }

    @Override
    public PaymentResult charge(PaymentRequest request) {
        if (!isConfigured()) {
            return unavailable("Stripe no está configurado");
        }
        return unavailable("La conexión real con Stripe aún no está implementada");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        return unavailable("Los reembolsos reales de Stripe aún no están implementados");
    }

    @Override
    public PaymentResult retrieve(String transactionId) {
        return unavailable("La consulta real de Stripe aún no está implementada");
    }

    @Override
    public boolean isConfigured() {
        return false;
    }

    private PaymentResult unavailable(String message) {
        log.error(message);
        return new PaymentResult(false, null, message, "NOT_IMPLEMENTED");
    }
}
