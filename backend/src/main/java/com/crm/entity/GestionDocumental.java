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
@Table(name = "gestion_documental")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestionDocumental {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    private String nombre;
    private String descripcion;
    private String tipo;
    private String categoria;
    private String estado;
    
    @Column(name = "url_archivo")
    private String urlArchivo;
    
    @Column(name = "tamano_kb")
    private Long tamanoKb;
    
    @Column(name = "extension")
    private String extension;
    
    @Column(name = "cliente_id")
    private Long clienteId;
    
    private String etiquetas;
    
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;
    
    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;
    
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
