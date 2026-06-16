package com.crm.repository;

import com.crm.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    List<Factura> findByEstado(String estado);
    List<Factura> findByTipo(String tipo);
    List<Factura> findByClienteId(Long clienteId);
    List<Factura> findByVentaId(Long ventaId);
    List<Factura> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
    List<Factura> findByFechaVencimientoBefore(LocalDate fecha);
    Optional<Factura> findByNumero(String numero);
}
