/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    @Column(nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String descripcion;
    
    @NotNull(message = "El monto es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal descuento;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal impuesto;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal total;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal comision;
    
    private String vendedor;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado = EstadoVenta.PENDIENTE;
    
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago = MetodoPago.TRANSFERENCIA;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaCierre;
    
    private Long cotizacionId;
    
    public enum EstadoVenta {
        PENDIENTE, EN_PROCESO, CERRADA, CANCELADA
    }
    
    public enum MetodoPago {
        EFECTIVO, TARJETA, TRANSFERENCIA, CHEQUE
    }
}
