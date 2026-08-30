/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "billing_invoice_id")
    private Long billingInvoiceId;

    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @Column(nullable = false)
    private String gateway;

    @Column(nullable = false)
    private String gatewayTransactionId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "gateway_response")
    @Lob
    private String gatewayResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }

    public enum PaymentGateway {
        STRIPE, WOMPI, MERCADO_PAGO, PAYU, MANUAL
    }
}
