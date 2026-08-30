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
@Table(name = "campos_personalizados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampoPersonalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false)
    private String entidad;

    @Column(nullable = false)
    private String nombreCampo;

    @Column(nullable = false)
    private String etiqueta;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "valor_defecto")
    private String valorDefecto;

    @Column(name = "opciones", columnDefinition = "TEXT")
    private String opciones;

    @Column(name = "es_requerido")
    private Boolean esRequerido = false;

    @Column(name = "es_busquable")
    private Boolean esBusquable = false;

    @Column(name = "es_visible_lista")
    private Boolean esVisibleLista = false;

    @Column(name = "orden")
    private Integer orden = 0;

    @Column(name = "validacion_regex")
    private String validacionRegex;

    @Column(name = "texto_ayuda")
    private String textoAyuda;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Tipo { TEXTO, NUMERO, FECHA, LISTA, MULTI_LISTA, BOOLEANO, AREA_TEXTO, FORMULA, TELEFONO, EMAIL, URL }
}
