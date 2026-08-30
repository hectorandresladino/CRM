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
@Table(name = "formularios_web")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioWeb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "campos", columnDefinition = "TEXT", nullable = false)
    private String campos;

    @Column(name = "destino_prospecto")
    private Boolean destinoProspecto = true;

    @Column(name = "destino_cliente")
    private Boolean destinoCliente = false;

    @Column(name = "asignar_a")
    private String asignarA;

    @Column(name = "mensaje_exito")
    private String mensajeExito;

    @Column(name = "redireccion_url")
    private String redireccionUrl;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "embed_token", nullable = false, unique = true)
    private String embedToken;

    @Column(name = "total_envios")
    private Integer totalEnvios = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
