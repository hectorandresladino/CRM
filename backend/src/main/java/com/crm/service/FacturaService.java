/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Factura;
import com.crm.repository.FacturaRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
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
        return facturaRepository.findByTenantId(tenantId());
    }
    
    public Optional<Factura> findById(Long id) {
        return facturaRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Factura save(Factura factura) {
        factura.setTenantId(tenantId());
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
        Factura existing = facturaRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Factura"));
        factura.setId(existing.getId());
        factura.setTenantId(existing.getTenantId());
        return facturaRepository.save(factura);
    }
    
    public void delete(Long id) {
        Factura factura = facturaRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Factura"));
        facturaRepository.delete(factura);
    }
    
    public List<Factura> findByClienteId(Long clienteId) {
        return facturaRepository.findByTenantIdAndClienteId(tenantId(), clienteId);
    }
    
    public List<Factura> findByEstado(String estado) {
        return facturaRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Factura> findVencidas() {
        return facturaRepository.findByTenantIdAndFechaVencimientoBefore(tenantId(), LocalDate.now());
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
