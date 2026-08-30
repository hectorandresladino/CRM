/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ReglaAutomatica;
import com.crm.repository.ReglaAutomaticaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReglaAutomaticaService {
    private final ReglaAutomaticaRepository repository;

    public List<ReglaAutomatica> findAll(Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    public ReglaAutomatica save(ReglaAutomatica regla) {
        return repository.save(regla);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public ReglaAutomatica toggle(Long id) {
        ReglaAutomatica regla = repository.findById(id).orElseThrow(() -> new RuntimeException("Regla no encontrada"));
        regla.setEsActiva(!regla.getEsActiva());
        return repository.save(regla);
    }

    public void recordExecution(Long id) {
        repository.findById(id).ifPresent(r -> {
            r.setTotalEjecuciones(r.getTotalEjecuciones() + 1);
            r.setUltimaEjecucion(LocalDateTime.now());
            repository.save(r);
        });
    }
}
