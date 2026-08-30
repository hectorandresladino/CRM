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
public class MercadoPagoProvider implements PaymentProvider {

    @Value("${billing.mercadopago.access-token:}")
    private String accessToken;

    @Override
    public String getProviderName() { return "MERCADO_PAGO"; }

    @Override
    public PaymentResult charge(PaymentRequest request) {
        return unavailable("La conexión real con Mercado Pago aún no está implementada");
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        return unavailable("Los reembolsos reales de Mercado Pago aún no están implementados");
    }

    @Override
    public PaymentResult retrieve(String transactionId) {
        return unavailable("La consulta real de Mercado Pago aún no está implementada");
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
