/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Prospecto;
import com.crm.repository.ProspectoRepository;
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
        return prospectoRepository.findAll();
    }
    
    public Optional<Prospecto> findById(Long id) {
        return prospectoRepository.findById(id);
    }
    
    public Prospecto save(Prospecto prospecto) {
        if (prospectoRepository.existsByEmail(prospecto.getEmail())) {
            throw new RuntimeException("Ya existe un prospecto con ese email");
        }
        return prospectoRepository.save(prospecto);
    }
    
    public Prospecto update(Long id, Prospecto prospecto) {
        Prospecto existingProspecto = prospectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prospecto no encontrado"));
        
        if (!existingProspecto.getEmail().equals(prospecto.getEmail()) && 
            prospectoRepository.existsByEmail(prospecto.getEmail())) {
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
        if (!prospectoRepository.existsById(id)) {
            throw new RuntimeException("Prospecto no encontrado");
        }
        prospectoRepository.deleteById(id);
    }
    
    public List<Prospecto> findByEstado(Prospecto.EstadoProspecto estado) {
        return prospectoRepository.findByEstado(estado);
    }
    
    public List<Prospecto> findByPrioridad(Prospecto.PrioridadProspecto prioridad) {
        return prospectoRepository.findByPrioridad(prioridad);
    }
    
    public List<Prospecto> buscarPorNombre(String nombre, String apellido) {
        return prospectoRepository.buscarPorNombre(nombre, apellido);
    }
    
    public List<Prospecto> buscarPorEmpresa(String empresa) {
        return prospectoRepository.buscarPorEmpresa(empresa);
    }
    
    public Prospecto actualizarEstado(Long id, Prospecto.EstadoProspecto estado) {
        Prospecto prospecto = prospectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prospecto no encontrado"));
        prospecto.setEstado(estado);
        if (estado == Prospecto.EstadoProspecto.CERRADO) {
            prospecto.setFechaConversion(LocalDateTime.now());
        }
        return prospectoRepository.save(prospecto);
    }
}
