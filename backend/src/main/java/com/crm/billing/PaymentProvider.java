/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.billing;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProvider {

    String getProviderName();

    PaymentResult charge(PaymentRequest request);

    PaymentResult refund(String transactionId, BigDecimal amount);

    PaymentResult retrieve(String transactionId);

    boolean isConfigured();

    record PaymentRequest(
        String customerId,
        String paymentMethodToken,
        BigDecimal amount,
        String currency,
        String description,
        Map<String, String> metadata
    ) {}

    record PaymentResult(
        boolean success,
        String transactionId,
        String errorMessage,
        String providerResponse
    ) {}
}
