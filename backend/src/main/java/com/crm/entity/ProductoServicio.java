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
@Table(name = "productos_servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private String familia;

    @Column(name = "sub_familia")
    private String subFamilia;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private String moneda;

    @Column(name = "costo", precision = 15, scale = 2)
    private BigDecimal costo;

    @Column(name = "impuesto_pct", precision = 5, scale = 2)
    private BigDecimal impuestoPct;

    @Column(name = "descuento_max_pct", precision = 5, scale = 2)
    private BigDecimal descuentoMaxPct;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "es_destacable")
    private Boolean esDestacable = false;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "atributos", columnDefinition = "TEXT")
    private String atributos;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Tipo { PRODUCTO, SERVICIO, SUSCRIPCION, BUNDLE }
}
