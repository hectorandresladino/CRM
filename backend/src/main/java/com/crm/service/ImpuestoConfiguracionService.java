/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ImpuestoConfiguracion;
import com.crm.repository.ImpuestoConfiguracionRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImpuestoConfiguracionService {
    private final ImpuestoConfiguracionRepository repository;

    public List<ImpuestoConfiguracion> findAll(Long tenantId) {
        return repository.findByTenantIdAndEsActivo(tid(), true);
    }

    public List<ImpuestoConfiguracion> findByPais(Long tenantId, String pais) {
        return repository.findByTenantIdAndPais(tid(), pais);
    }

    public ImpuestoConfiguracion save(ImpuestoConfiguracion impuesto) {
        impuesto.setTenantId(tid());
        return repository.save(impuesto);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Impuesto no encontrado")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
