package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reglas_automaticas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaAutomatica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private String entidad;

    @Column(nullable = false)
    private String evento;

    @Column(name = "condiciones", columnDefinition = "TEXT", nullable = false)
    private String condiciones;

    @Column(name = "acciones", columnDefinition = "TEXT", nullable = false)
    private String acciones;

    @Column(name = "es_activa")
    private Boolean esActiva = true;

    @Column(name = "prioridad")
    private Integer prioridad = 0;

    @Column(name = "total_ejecuciones")
    private Integer totalEjecuciones = 0;

    @Column(name = "ultima_ejecucion")
    private LocalDateTime ultimaEjecucion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
