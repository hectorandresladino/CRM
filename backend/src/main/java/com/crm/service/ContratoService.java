/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Contrato;
import com.crm.repository.ContratoRepository;
import com.crm.security.TenantAccessDeniedException;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratoService {
    
    private final ContratoRepository contratoRepository;
    
    public List<Contrato> findAll() {
        return contratoRepository.findByTenantId(tenantId());
    }
    
    public Optional<Contrato> findById(Long id) {
        return contratoRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Contrato save(Contrato contrato) {
        contrato.setTenantId(tenantId());
        return contratoRepository.save(contrato);
    }
    
    public Contrato update(Long id, Contrato contrato) {
        Contrato existing = contratoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Contrato"));
        contrato.setId(existing.getId());
        contrato.setTenantId(existing.getTenantId());
        return contratoRepository.save(contrato);
    }
    
    public void delete(Long id) {
        Contrato contrato = contratoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Contrato"));
        contratoRepository.delete(contrato);
    }
    
    public List<Contrato> findByClienteId(Long clienteId) {
        return contratoRepository.findByTenantIdAndClienteId(tenantId(), clienteId);
    }
    
    public List<Contrato> findByEstado(String estado) {
        return contratoRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Contrato> findPorVencer() {
        return contratoRepository.findByTenantIdAndFechaFinBefore(tenantId(), LocalDate.now().plusDays(30));
    }

    private Long tenantId() { return TenantContext.requireCurrentTenant(); }
}
