/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.CampoPersonalizado;
import com.crm.repository.CampoPersonalizadoRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CampoPersonalizadoService {
    private final CampoPersonalizadoRepository repository;

    public List<CampoPersonalizado> findByEntidad(Long tenantId, String entidad) {
        return repository.findByTenantIdAndEntidadOrderByOrden(tid(), entidad);
    }

    public CampoPersonalizado save(CampoPersonalizado campo) {
        campo.setTenantId(tid());
        return repository.save(campo);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
