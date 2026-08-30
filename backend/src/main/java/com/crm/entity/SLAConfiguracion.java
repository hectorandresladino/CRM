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

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_configuraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SLAConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "prioridad")
    private String prioridad;

    @Column(name = "tiempo_respuesta_horas")
    private Integer tiempoRespuestaHoras;

    @Column(name = "tiempo_resolucion_horas")
    private Integer tiempoResolucionHoras;

    @Column(name = "escalar_a")
    private String escalarA;

    @Column(name = "horas_desde_escalar")
    private Integer horasDesdeEscalar;

    @Column(name = "activo")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Prioridad { CRITICA, ALTA, MEDIA, BAJA }
}
