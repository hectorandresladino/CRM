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
@Table(name = "actividades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "prospecto_id")
    private Long prospectoId;

    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "asignado_a")
    private String asignadoA;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "estado")
    private String estado;

    @Column(name = "prioridad")
    private String prioridad;

    @Column(name = "resultado")
    private String resultado;

    @Column(name = "recordatorio_minutos")
    private Integer recordatorioMinutos;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "participantes")
    private String participantes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Tipo { TAREA, LLAMADA, REUNION, EMAIL, VISITA, WHATSAPP, NOTA }
    public enum Estado { PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA, POSTPUESTA }
    public enum Prioridad { ALTA, MEDIA, BAJA }
}
