/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.GestionDocumental;
import com.crm.repository.GestionDocumentalRepository;
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
public class GestionDocumentalService {
    
    private final GestionDocumentalRepository documentoRepository;
    
    public List<GestionDocumental> findAll() {
        return documentoRepository.findByTenantId(tid());
    }
    
    public Optional<GestionDocumental> findById(Long id) {
        return documentoRepository.findByTenantIdAndId(tid(), id);
    }
    
    public GestionDocumental save(GestionDocumental documento) {
        documento.setTenantId(tid());
        documento.setFechaSubida(LocalDateTime.now());
        return documentoRepository.save(documento);
    }
    
    public GestionDocumental update(Long id, GestionDocumental documento) {
        documentoRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        documento.setId(id);
        documento.setTenantId(tid());
        return documentoRepository.save(documento);
    }
    
    public void delete(Long id) {
        documentoRepository.delete(documentoRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado")));
    }
    
    public List<GestionDocumental> findByClienteId(Long clienteId) {
        return documentoRepository.findByTenantIdAndClienteId(tid(), clienteId);
    }
    
    public List<GestionDocumental> findByCategoria(String categoria) {
        return documentoRepository.findByTenantIdAndCategoria(tid(), categoria);
    }
    
    public List<GestionDocumental> findPorVencer() {
        return documentoRepository.findByTenantIdAndFechaVencimientoBefore(tid(), LocalDateTime.now().plusDays(7));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
