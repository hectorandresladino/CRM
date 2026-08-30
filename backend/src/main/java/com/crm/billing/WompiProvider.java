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
        return unavailable("La conexión real con Wompi aún no está implementada");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        return unavailable("Los reembolsos reales de Wompi aún no están implementados");
    }

    @Override
    public PaymentResult retrieve(String transactionId) {
        return unavailable("La consulta real de Wompi aún no está implementada");
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
