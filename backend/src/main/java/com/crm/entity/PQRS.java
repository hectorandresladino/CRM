package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pqrs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PQRS {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String codigo;
    private String asunto;
    private String descripcion;
    private String tipo;
    private String prioridad;
    private String estado;
    
    @Column(name = "cliente_id")
    private Long clienteId;
    
    private String canal;
    private String asignadoA;
    
    private String resolucion;
    private String notas;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
    
    @Column(name = "tiempo_respuesta_horas")
    private Integer tiempoRespuestaHoras;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaCreacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
