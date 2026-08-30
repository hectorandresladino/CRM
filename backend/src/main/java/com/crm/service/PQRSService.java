/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.PQRS;
import com.crm.repository.PQRSRepository;
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
        return pqrsRepository.findAll();
    }
    
    public Optional<PQRS> findById(Long id) {
        return pqrsRepository.findById(id);
    }
    
    public PQRS save(PQRS pqrs) {
        return pqrsRepository.save(pqrs);
    }
    
    public PQRS update(Long id, PQRS pqrs) {
        pqrs.setId(id);
        return pqrsRepository.save(pqrs);
    }
    
    public void delete(Long id) {
        pqrsRepository.deleteById(id);
    }
    
    public PQRS resolver(Long id, String resolucion) {
        Optional<PQRS> pqrsOpt = pqrsRepository.findById(id);
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
        return pqrsRepository.findByClienteId(clienteId);
    }
    
    public List<PQRS> findByEstado(String estado) {
        return pqrsRepository.findByEstado(estado);
    }
    
    public List<PQRS> findByPrioridad(String prioridad) {
        return pqrsRepository.findByPrioridad(prioridad);
    }
}
