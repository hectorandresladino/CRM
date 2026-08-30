/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.GestionDocumental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GestionDocumentalRepository extends JpaRepository<GestionDocumental, Long> {
    
    List<GestionDocumental> findByEstado(String estado);
    List<GestionDocumental> findByTipo(String tipo);
    List<GestionDocumental> findByCategoria(String categoria);
    List<GestionDocumental> findByClienteId(Long clienteId);
    List<GestionDocumental> findByEtiquetasContaining(String etiqueta);
    List<GestionDocumental> findByFechaVencimientoBefore(LocalDateTime fecha);
}
