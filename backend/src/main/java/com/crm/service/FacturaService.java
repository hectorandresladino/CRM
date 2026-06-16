package com.crm.service;

import com.crm.entity.Factura;
import com.crm.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturaService {
    
    private final FacturaRepository facturaRepository;
    
    public List<Factura> findAll() {
        return facturaRepository.findAll();
    }
    
    public Optional<Factura> findById(Long id) {
        return facturaRepository.findById(id);
    }
    
    public Factura save(Factura factura) {
        if (factura.getTotal() == null && factura.getSubtotal() != null) {
            BigDecimal total = factura.getSubtotal();
            if (factura.getImpuesto() != null) {
                total = total.add(factura.getImpuesto());
            }
            factura.setTotal(total);
        }
        return facturaRepository.save(factura);
    }
    
    public Factura update(Long id, Factura factura) {
        factura.setId(id);
        return facturaRepository.save(factura);
    }
    
    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }
    
    public List<Factura> findByClienteId(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }
    
    public List<Factura> findByEstado(String estado) {
        return facturaRepository.findByEstado(estado);
    }
    
    public List<Factura> findVencidas() {
        return facturaRepository.findByFechaVencimientoBefore(LocalDate.now());
    }
}
