/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Prospecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProspectoRepository extends JpaRepository<Prospecto, Long> {
    
    Optional<Prospecto> findByTenantIdAndEmail(Long tenantId, String email);
    
    List<Prospecto> findByEstado(Prospecto.EstadoProspecto estado);
    
    List<Prospecto> findByPrioridad(Prospecto.PrioridadProspecto prioridad);
    
    @Query("SELECT p FROM Prospecto p WHERE p.nombre LIKE %:nombre% OR p.apellido LIKE %:apellido%")
    List<Prospecto> buscarPorNombre(@Param("nombre") String nombre, @Param("apellido") String apellido);
    
    @Query("SELECT p FROM Prospecto p WHERE p.empresa LIKE %:empresa%")
    List<Prospecto> buscarPorEmpresa(@Param("empresa") String empresa);
    
    @Query("SELECT p FROM Prospecto p WHERE p.estado = :estado AND p.prioridad = :prioridad")
    List<Prospecto> findByEstadoAndPrioridad(@Param("estado") Prospecto.EstadoProspecto estado, 
                                              @Param("prioridad") Prospecto.PrioridadProspecto prioridad);
    
    boolean existsByTenantIdAndEmail(Long tenantId, String email);
    List<Prospecto> findByTenantId(Long tenantId);
    Optional<Prospecto> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
    List<Prospecto> findByTenantIdAndEstado(Long tenantId, Prospecto.EstadoProspecto estado);
    List<Prospecto> findByTenantIdAndPrioridad(Long tenantId, Prospecto.PrioridadProspecto prioridad);
    List<Prospecto> findByTenantIdAndNombreContainingIgnoreCaseOrTenantIdAndApellidoContainingIgnoreCase(
            Long tenantIdForName, String nombre, Long tenantIdForLastName, String apellido);
    List<Prospecto> findByTenantIdAndEmpresaContainingIgnoreCase(Long tenantId, String empresa);
}
