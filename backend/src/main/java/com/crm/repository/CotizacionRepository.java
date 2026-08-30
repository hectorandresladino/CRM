/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    
    List<Cotizacion> findByClienteId(Long clienteId);
    
    List<Cotizacion> findByEstado(Cotizacion.EstadoCotizacion estado);
    
    List<Cotizacion> findByVendedor(String vendedor);
    
    List<Cotizacion> findByValidezBefore(LocalDate fecha);
    
    @Query("SELECT c FROM Cotizacion c WHERE c.cliente.id = :clienteId AND c.estado = :estado")
    List<Cotizacion> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, 
                                              @Param("estado") Cotizacion.EstadoCotizacion estado);
    
    @Query("SELECT c FROM Cotizacion c WHERE c.validez < :fecha AND c.estado = 'ENVIADA'")
    List<Cotizacion> findExpiredCotizaciones(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT COUNT(c) FROM Cotizacion c WHERE c.estado = :estado")
    Long countByEstado(@Param("estado") Cotizacion.EstadoCotizacion estado);
}
