/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findByTenantIdOrderByFechaProgramadaDesc(Long tenantId);
    List<Actividad> findByTenantIdAndAsignadoA(Long tenantId, String asignadoA);
    List<Actividad> findByTenantIdAndEstado(Long tenantId, String estado);
    List<Actividad> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    List<Actividad> findByTenantIdAndProspectoId(Long tenantId, Long prospectoId);
    Optional<Actividad> findByTenantIdAndId(Long tenantId, Long id);
}
