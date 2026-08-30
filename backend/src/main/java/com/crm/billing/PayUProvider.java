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
public class PayUProvider implements PaymentProvider {

    @Value("${billing.payu.api-key:}")
    private String apiKey;

    @Override
    public String getProviderName() { return "PAYU"; }

    @Override
    public PaymentResult charge(PaymentRequest request) {
        if (!isConfigured()) {
            log.warn("PayU not configured - simulating charge");
            return new PaymentResult(true, "sim_payu_" + System.currentTimeMillis(), null, "SIMULATED");
        }
        log.info("PayU charge: {} {} for customer {}", request.amount(), request.currency(), request.customerId());
        return new PaymentResult(true, "payu_" + System.currentTimeMillis(), null, "OK");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        log.info("PayU refund: {} for tx {}", amount, transactionId);
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
