/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "encuestas_satisfaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncuestaSatisfaccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    private String nombre;
    private String descripcion;
    private String tipo;
    private String estado;
    
    @Column(name = "cliente_id")
    private Long clienteId;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    
    @Column(name = "calificacion_general")
    private Integer calificacionGeneral;
    
    private String comentarios;
    
    @Column(name = "pregunta1")
    private Integer pregunta1;
    
    @Column(name = "pregunta2")
    private Integer pregunta2;
    
    @Column(name = "pregunta3")
    private Integer pregunta3;
    
    @Column(name = "pregunta4")
    private Integer pregunta4;
    
    @Column(name = "pregunta5")
    private Integer pregunta5;
    
    @Column(name = "recomendaria")
    private Boolean recomendaria;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
