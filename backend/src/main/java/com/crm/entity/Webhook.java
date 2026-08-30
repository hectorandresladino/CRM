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
@Table(name = "webhooks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String evento;

    @Column(name = "metodo_http")
    private String metodoHttp = "POST";

    @Column(name = "headers", columnDefinition = "TEXT")
    private String headers;

    @Column(name = "secret_token")
    private String secretToken;

    @Column(name = "es_activo")
    private Boolean esActivo = true;

    @Column(name = "ultimo_envio")
    private LocalDateTime ultimoEnvio;

    @Column(name = "ultimo_estado")
    private Integer ultimoEstado;

    @Column(name = "total_envios")
    private Integer totalEnvios = 0;

    @Column(name = "total_exitos")
    private Integer totalExitos = 0;

    @Column(name = "total_fallos")
    private Integer totalFallos = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
