package com.crm.repository;

import com.crm.entity.ServicioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServicioClienteRepository extends JpaRepository<ServicioCliente, Long> {
    
    List<ServicioCliente> findByClienteId(Long clienteId);
    
    List<ServicioCliente> findByEstado(ServicioCliente.EstadoServicio estado);
    
    List<ServicioCliente> findByTipo(ServicioCliente.TipoPQRS tipo);
    
    List<ServicioCliente> findByPrioridad(ServicioCliente.PrioridadPQRS prioridad);
    
    List<ServicioCliente> findByAsignadoA(String asignadoA);
    
    @Query("SELECT s FROM ServicioCliente s WHERE s.cliente.id = :clienteId AND s.estado = :estado")
    List<ServicioCliente> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, 
                                                   @Param("estado") ServicioCliente.EstadoServicio estado);
    
    @Query("SELECT s FROM ServicioCliente s WHERE s.prioridad = :prioridad AND s.estado NOT IN ('RESUELTO', 'CERRADO')")
    List<ServicioCliente> findUrgentesAbiertos(@Param("prioridad") ServicioCliente.PrioridadPQRS prioridad);
    
    @Query("SELECT COUNT(s) FROM ServicioCliente s WHERE s.estado = :estado")
    Long countByEstado(@Param("estado") ServicioCliente.EstadoServicio estado);
    
    @Query("SELECT s FROM ServicioCliente s WHERE s.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<ServicioCliente> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                      @Param("fechaFin") LocalDateTime fechaFin);
}
