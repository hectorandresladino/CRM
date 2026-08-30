/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByClienteId(Long clienteId);
    
    List<Pedido> findByEstado(Pedido.EstadoPedido estado);
    
    List<Pedido> findByVendedor(String vendedor);
    
    List<Pedido> findByFechaEntregaEstimadaBefore(LocalDate fecha);
    
    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    List<Pedido> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, 
                                          @Param("estado") Pedido.EstadoPedido estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaEntregaEstimada < :fecha AND p.estado NOT IN ('ENTREGADO', 'CANCELADO')")
    List<Pedido> findPedidosAtrasados(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") Pedido.EstadoPedido estado);
}
