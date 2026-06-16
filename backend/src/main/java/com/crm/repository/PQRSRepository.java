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
}
