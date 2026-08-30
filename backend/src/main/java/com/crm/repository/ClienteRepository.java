/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByEmail(String email);
    
    List<Cliente> findByEstado(Cliente.EstadoCliente estado);
    
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:nombre% OR c.apellido LIKE %:apellido%")
    List<Cliente> buscarPorNombre(@Param("nombre") String nombre, @Param("apellido") String apellido);
    
    @Query("SELECT c FROM Cliente c WHERE c.empresa LIKE %:empresa%")
    List<Cliente> buscarPorEmpresa(@Param("empresa") String empresa);
    
    @Query("SELECT c FROM Cliente c WHERE c.identificacion = :identificacion")
    Optional<Cliente> findByIdentificacion(@Param("identificacion") String identificacion);
    
    boolean existsByEmail(String email);
    
    boolean existsByIdentificacion(String identificacion);

    long countByTenantId(Long tenantId);
}
