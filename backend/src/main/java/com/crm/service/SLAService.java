/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.SLAConfiguracion;
import com.crm.repository.SLAConfiguracionRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SLAService {
    private final SLAConfiguracionRepository repository;

    public List<SLAConfiguracion> findAll(Long tenantId) {
        return repository.findByTenantIdAndActivo(tid(), true);
    }

    public SLAConfiguracion save(SLAConfiguracion sla) {
        sla.setTenantId(tid());
        return repository.save(sla);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("SLA no encontrado")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
