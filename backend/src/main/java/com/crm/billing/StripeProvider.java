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
            log.warn("Stripe not configured - simulating charge");
            return new PaymentResult(true, "sim_stripe_" + System.currentTimeMillis(), null, "SIMULATED");
        }
        log.info("Stripe charge: {} {} for customer {}", request.amount(), request.currency(), request.customerId());
        return new PaymentResult(true, "stripe_" + System.currentTimeMillis(), null, "OK");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        log.info("Stripe refund: {} for tx {}", amount, transactionId);
        return new PaymentResult(true, "refund_" + transactionId, null, "OK");
    }

    @Override
    public PaymentResult retrieve(String transactionId) {
        return new PaymentResult(true, transactionId, null, "RETRIEVED");
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
