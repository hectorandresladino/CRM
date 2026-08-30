/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "factura_id")
    private Long facturaId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(nullable = false)
    private String referencia;

    @Column(name = "proveedor_pago")
    private String proveedorPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String moneda;

    @Column(nullable = false)
    private String estado;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum ProveedorPago { WOMPI, PAYU, MERCADO_PAGO, STRIPE, PAYPAL, MANUAL }
    public enum MetodoPago { TARJETA, PSE, EFECTY, TRANSFERENCIA, NEQUI, PAYPAL }
    public enum Estado { PENDIENTE, APROBADO, RECHAZADO, REEMBOLSADO, FALLIDO }
}
