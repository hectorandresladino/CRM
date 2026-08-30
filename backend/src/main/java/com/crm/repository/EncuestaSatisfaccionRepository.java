/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.EncuestaSatisfaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EncuestaSatisfaccionRepository extends JpaRepository<EncuestaSatisfaccion, Long> {
    
    List<EncuestaSatisfaccion> findByTenantId(Long tenantId);
    List<EncuestaSatisfaccion> findByEstado(String estado);
    List<EncuestaSatisfaccion> findByTipo(String tipo);
    List<EncuestaSatisfaccion> findByClienteId(Long clienteId);
    List<EncuestaSatisfaccion> findByFechaEnvioBetween(LocalDateTime inicio, LocalDateTime fin);
    List<EncuestaSatisfaccion> findByCalificacionGeneralGreaterThan(Integer calificacion);
}
