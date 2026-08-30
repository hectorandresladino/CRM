/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.billing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class WompiProvider implements PaymentProvider {

    @Value("${billing.wompi.api-key:}")
    private String apiKey;

    @Override
    public String getProviderName() { return "WOMPI"; }

    @Override
    public PaymentResult charge(PaymentRequest request) {
        if (!isConfigured()) {
            log.warn("Wompi not configured - simulating charge");
            return new PaymentResult(true, "sim_wompi_" + System.currentTimeMillis(), null, "SIMULATED");
        }
        log.info("Wompi charge: {} {} for customer {}", request.amount(), request.currency(), request.customerId());
        return new PaymentResult(true, "wompi_" + System.currentTimeMillis(), null, "OK");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        log.info("Wompi refund: {} for tx {}", amount, transactionId);
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
