package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mesa_ayuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaAyuda {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    private String ticket;
    private String asunto;
    private String descripcion;
    private String categoria;
    private String prioridad;
    private String estado;
    
    @Column(name = "cliente_id")
    private Long clienteId;
    
    private String canal;
    private String asignadoA;
    
    private String solucion;
    private String notas;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @Column(name = "tiempo_resolucion_minutos")
    private Integer tiempoResolucionMinutos;
    
    @Column(name = "satisfaccion_cliente")
    private Integer satisfaccionCliente;
    
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
