/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "prospectos", uniqueConstraints =
        @UniqueConstraint(name = "uk_prospectos_tenant_email", columnNames = {"tenant_id", "email"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prospecto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;
    
    @Email(message = "Email inválido")
    @Column
    private String email;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 20)
    private String celular;
    
    private String empresa;
    
    private String cargo;
    
    private String sector;
    
    @Column(columnDefinition = "TEXT")
    private String origen;
    
    @Column(columnDefinition = "TEXT")
    private String interes;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProspecto estado = EstadoProspecto.NUEVO;
    
    @Enumerated(EnumType.STRING)
    private PrioridadProspecto prioridad = PrioridadProspecto.MEDIA;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaContacto;
    
    private LocalDateTime fechaConversion;
    
    private Long clienteId;
    
    public enum EstadoProspecto {
        NUEVO, CONTACTADO, CALIFICADO, PROPUESTA, NEGOCIACION, CERRADO, PERDIDO
    }
    
    public enum PrioridadProspecto {
        BAJA, MEDIA, ALTA, URGENTE
    }
}
