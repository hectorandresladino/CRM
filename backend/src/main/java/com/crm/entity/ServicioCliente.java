package com.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "servicios_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioCliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(nullable = false)
    private String codigo;
    
    @NotBlank(message = "El asunto es obligatorio")
    @Column(nullable = false)
    private String asunto;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPQRS tipo = TipoPQRS.PREGUNTA;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadPQRS prioridad = PrioridadPQRS.MEDIA;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CanalPQRS canal = CanalPQRS.EMAIL;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoServicio estado = EstadoServicio.ABIERTO;
    
    private String asignadoA;
    
    @Column(columnDefinition = "TEXT")
    private String resolucion;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    private LocalDateTime fechaAsignacion;
    
    private LocalDateTime fechaCierre;
    
    private LocalDateTime fechaRespuesta;
    
    public enum TipoPQRS {
        PREGUNTA, QUEJA, RECLAMO, SUGERENCIA, FELICITACION
    }
    
    public enum PrioridadPQRS {
        BAJA, MEDIA, ALTA, URGENTE, CRITICA
    }
    
    public enum CanalPQRS {
        EMAIL, TELEFONO, CHAT, WHATSAPP, RED_SOCIAL, PRESENCIAL
    }
    
    public enum EstadoServicio {
        ABIERTO, ASIGNADO, EN_PROCESO, ESPERA_RESPUESTA, RESUELTO, CERRADO
    }
}
