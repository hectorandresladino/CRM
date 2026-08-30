/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByClienteId(Long clienteId);
    
    List<Venta> findByEstado(Venta.EstadoVenta estado);
    
    List<Venta> findByVendedor(String vendedor);
    
    @Query("SELECT v FROM Venta v WHERE v.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                            @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v WHERE v.cliente.id = :clienteId AND v.estado = :estado")
    List<Venta> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, 
                                         @Param("estado") Venta.EstadoVenta estado);
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.estado = 'CERRADA'")
    Double sumTotalVentasCerradas();
    
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = :estado")
    Long countByEstado(@Param("estado") Venta.EstadoVenta estado);
}
