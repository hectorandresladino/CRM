/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.PQRS;
import com.crm.repository.PQRSRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PQRSService {
    
    private final PQRSRepository pqrsRepository;
    
    public List<PQRS> findAll() {
        return pqrsRepository.findByTenantId(tid());
    }
    
    public Optional<PQRS> findById(Long id) {
        return pqrsRepository.findByTenantIdAndId(tid(), id);
    }
    
    public PQRS save(PQRS pqrs) {
        pqrs.setTenantId(tid());
        return pqrsRepository.save(pqrs);
    }
    
    public PQRS update(Long id, PQRS pqrs) {
        pqrsRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrada"));
        pqrs.setId(id);
        pqrs.setTenantId(tid());
        return pqrsRepository.save(pqrs);
    }
    
    public void delete(Long id) {
        pqrsRepository.delete(pqrsRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("PQRS no encontrada")));
    }
    
    public PQRS resolver(Long id, String resolucion) {
        Optional<PQRS> pqrsOpt = pqrsRepository.findByTenantIdAndId(tid(), id);
        if (pqrsOpt.isPresent()) {
            PQRS pqrs = pqrsOpt.get();
            pqrs.setResolucion(resolucion);
            pqrs.setEstado("RESUELTO");
            pqrs.setFechaResolucion(LocalDateTime.now());
            return pqrsRepository.save(pqrs);
        }
        throw new RuntimeException("PQRS no encontrado");
    }
    
    public List<PQRS> findByClienteId(Long clienteId) {
        return pqrsRepository.findByTenantIdAndClienteId(tid(), clienteId);
    }
    
    public List<PQRS> findByEstado(String estado) {
        return pqrsRepository.findByTenantIdAndEstado(tid(), estado);
    }
    
    public List<PQRS> findByPrioridad(String prioridad) {
        return pqrsRepository.findByTenantIdAndPrioridad(tid(), prioridad);
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
