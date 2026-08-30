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
@Table(name = "impuestos_configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImpuestoConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String pais;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "tipo_impuesto")
    private String tipoImpuesto;

    @Column(name = "es_incluido")
    private Boolean esIncluido = false;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "descripcion")
    private String descripcion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum TipoImpuesto { IVA, VAT, GST, SALES_TAX, RETENCION, CONSUMO }
}
