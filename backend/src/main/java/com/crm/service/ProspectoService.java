/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Prospecto;
import com.crm.repository.ProspectoRepository;
import com.crm.security.TenantAccessDeniedException;
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
public class ProspectoService {
    
    private final ProspectoRepository prospectoRepository;
    
    public List<Prospecto> findAll() {
        return prospectoRepository.findByTenantId(tenantId());
    }
    
    public Optional<Prospecto> findById(Long id) {
        return prospectoRepository.findByIdAndTenantId(id, tenantId());
    }
    
    public Prospecto save(Prospecto prospecto) {
        Long tenantId = tenantId();
        prospecto.setTenantId(tenantId);
        if (prospectoRepository.existsByTenantIdAndEmail(tenantId, prospecto.getEmail())) {
            throw new RuntimeException("Ya existe un prospecto con ese email");
        }
        return prospectoRepository.save(prospecto);
    }
    
    public Prospecto update(Long id, Prospecto prospecto) {
        Long tenantId = tenantId();
        Prospecto existingProspecto = prospectoRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TenantAccessDeniedException("Prospecto"));
        
        if (!existingProspecto.getEmail().equals(prospecto.getEmail()) && 
            prospectoRepository.existsByTenantIdAndEmail(tenantId, prospecto.getEmail())) {
            throw new RuntimeException("Ya existe un prospecto con ese email");
        }
        
        existingProspecto.setNombre(prospecto.getNombre());
        existingProspecto.setApellido(prospecto.getApellido());
        existingProspecto.setEmail(prospecto.getEmail());
        existingProspecto.setTelefono(prospecto.getTelefono());
        existingProspecto.setCelular(prospecto.getCelular());
        existingProspecto.setEmpresa(prospecto.getEmpresa());
        existingProspecto.setCargo(prospecto.getCargo());
        existingProspecto.setSector(prospecto.getSector());
        existingProspecto.setOrigen(prospecto.getOrigen());
        existingProspecto.setInteres(prospecto.getInteres());
        existingProspecto.setNotas(prospecto.getNotas());
        existingProspecto.setEstado(prospecto.getEstado());
        existingProspecto.setPrioridad(prospecto.getPrioridad());
        existingProspecto.setFechaContacto(prospecto.getFechaContacto());
        
        if (prospecto.getEstado() == Prospecto.EstadoProspecto.CERRADO && 
            existingProspecto.getFechaConversion() == null) {
            existingProspecto.setFechaConversion(LocalDateTime.now());
        }
        
        return prospectoRepository.save(existingProspecto);
    }
    
    public void delete(Long id) {
        Prospecto prospecto = prospectoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Prospecto"));
        prospectoRepository.delete(prospecto);
    }
    
    public List<Prospecto> findByEstado(Prospecto.EstadoProspecto estado) {
        return prospectoRepository.findByTenantIdAndEstado(tenantId(), estado);
    }
    
    public List<Prospecto> findByPrioridad(Prospecto.PrioridadProspecto prioridad) {
        return prospectoRepository.findByTenantIdAndPrioridad(tenantId(), prioridad);
    }
    
    public List<Prospecto> buscarPorNombre(String nombre, String apellido) {
        Long tenantId = tenantId();
        return prospectoRepository.findByTenantIdAndNombreContainingIgnoreCaseOrTenantIdAndApellidoContainingIgnoreCase(
                tenantId, nombre, tenantId, apellido);
    }
    
    public List<Prospecto> buscarPorEmpresa(String empresa) {
        return prospectoRepository.findByTenantIdAndEmpresaContainingIgnoreCase(tenantId(), empresa);
    }
    
    public Prospecto actualizarEstado(Long id, Prospecto.EstadoProspecto estado) {
        Prospecto prospecto = prospectoRepository.findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new TenantAccessDeniedException("Prospecto"));
        prospecto.setEstado(estado);
        if (estado == Prospecto.EstadoProspecto.CERRADO) {
            prospecto.setFechaConversion(LocalDateTime.now());
        }
        return prospectoRepository.save(prospecto);
    }

    private Long tenantId() {
        return TenantContext.requireCurrentTenant();
    }
}
