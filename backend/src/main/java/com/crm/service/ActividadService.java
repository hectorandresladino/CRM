/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Actividad;
import com.crm.repository.ActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActividadService {
    private final ActividadRepository repository;

    public List<Actividad> findAll(Long tenantId) {
        return repository.findByTenantIdOrderByFechaProgramadaDesc(tenantId);
    }

    public List<Actividad> findByUser(Long tenantId, String user) {
        return repository.findByTenantIdAndAsignadoA(tenantId, user);
    }

    public List<Actividad> findByStatus(Long tenantId, String status) {
        return repository.findByTenantIdAndEstado(tenantId, status);
    }

    public Actividad save(Actividad actividad) {
        return repository.save(actividad);
    }

    public Actividad complete(Long id) {
        Actividad a = repository.findById(id).orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        a.setEstado("COMPLETADA");
        a.setFechaCompletada(LocalDateTime.now());
        return repository.save(a);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
