/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    
    List<Contrato> findByEstado(String estado);
    List<Contrato> findByTipo(String tipo);
    List<Contrato> findByClienteId(Long clienteId);
    List<Contrato> findByFechaFinBefore(LocalDate fecha);
    Optional<Contrato> findByCodigo(String codigo);
    List<Contrato> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
}
