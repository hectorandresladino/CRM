package com.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cotizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;
    
    @Column(nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String descripcion;
    
    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal descuento;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal impuesto;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal margen;
    
    private String vendedor;
    
    @Column(columnDefinition = "TEXT")
    private String terminos;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(nullable = false)
    private LocalDate validez;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCotizacion estado = EstadoCotizacion.BORRADOR;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaEnvio;
    
    private LocalDateTime fechaAprobacion;
    
    private Long ventaId;
    
    public enum EstadoCotizacion {
        BORRADOR, ENVIADA, APROBADA, RECHAZADA, EXPIRADA
    }
}
