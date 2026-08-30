/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ImpuestoConfiguracion;
import com.crm.repository.ImpuestoConfiguracionRepository;
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
        return repository.findByTenantIdAndEsActivo(tenantId, true);
    }

    public List<ImpuestoConfiguracion> findByPais(Long tenantId, String pais) {
        return repository.findByTenantIdAndPais(tenantId, pais);
    }

    public ImpuestoConfiguracion save(ImpuestoConfiguracion impuesto) {
        return repository.save(impuesto);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
