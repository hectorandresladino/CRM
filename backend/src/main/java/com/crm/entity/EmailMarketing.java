package com.crm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_marketing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMarketing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String asunto;
    private String contenido;
    private String estado;
    private String tipo;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;
    
    private String remitente;
    private String listaDestinatarios;
    
    @Column(name = "total_enviados")
    private Integer totalEnviados;
    
    @Column(name = "total_abiertos")
    private Integer totalAbiertos;
    
    @Column(name = "total_clicks")
    private Integer totalClicks;
    
    @Column(name = "tasa_apertura")
    private Double tasaApertura;
    
    @Column(name = "tasa_click")
    private Double tasaClick;
    
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
