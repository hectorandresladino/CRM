/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.PQRS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PQRSRepository extends JpaRepository<PQRS, Long> {
    
    List<PQRS> findByEstado(String estado);
    List<PQRS> findByTipo(String tipo);
    List<PQRS> findByPrioridad(String prioridad);
    List<PQRS> findByClienteId(Long clienteId);
    List<PQRS> findByCanal(String canal);
    List<PQRS> findByAsignadoA(String asignadoA);
    Optional<PQRS> findByCodigo(String codigo);
    List<PQRS> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<PQRS> findByTenantId(Long tenantId);
    Optional<PQRS> findByTenantIdAndId(Long tenantId, Long id);
    List<PQRS> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    List<PQRS> findByTenantIdAndEstado(Long tenantId, String estado);
    List<PQRS> findByTenantIdAndPrioridad(Long tenantId, String prioridad);
}
