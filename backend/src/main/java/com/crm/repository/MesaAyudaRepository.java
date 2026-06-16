package com.crm.repository;

import com.crm.entity.MesaAyuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MesaAyudaRepository extends JpaRepository<MesaAyuda, Long> {
    
    List<MesaAyuda> findByEstado(String estado);
    List<MesaAyuda> findByCategoria(String categoria);
    List<MesaAyuda> findByPrioridad(String prioridad);
    List<MesaAyuda> findByClienteId(Long clienteId);
    List<MesaAyuda> findByCanal(String canal);
    List<MesaAyuda> findByAsignadoA(String asignadoA);
    Optional<MesaAyuda> findByTicket(String ticket);
    List<MesaAyuda> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<MesaAyuda> findByEstadoNot(String estado);
}
