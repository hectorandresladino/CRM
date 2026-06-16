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
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    
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
    private BigDecimal costoEnvio;
    
    private String direccionEnvio;
    
    private String ciudadEnvio;
    
    private String paisEnvio;
    
    @Column(length = 10)
    private String codigoPostalEnvio;
    
    @Column(nullable = false)
    private LocalDate fechaEntregaEstimada;
    
    private LocalDate fechaEntregaReal;
    
    private String vendedor;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Column(columnDefinition = "TEXT")
    private String notasEnvio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Enumerated(EnumType.STRING)
    private MetodoEnvio metodoEnvio = MetodoEnvio.ESTANDAR;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaProcesamiento;
    
    private LocalDateTime fechaEnvio;
    
    private Long ventaId;
    
    private Long cotizacionId;
    
    public enum EstadoPedido {
        PENDIENTE, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO
    }
    
    public enum MetodoEnvio {
        ESTANDAR, EXPRESS, RECOGIDA
    }
}
