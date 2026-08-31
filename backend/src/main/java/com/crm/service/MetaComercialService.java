/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.MetaComercial;
import com.crm.repository.MetaComercialRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MetaComercialService {
    private final MetaComercialRepository repository;

    public List<MetaComercial> findAll(Long tenantId) {
        return repository.findByTenantId(tid());
    }

    public List<MetaComercial> findByYear(Long tenantId, Integer year) {
        return repository.findByTenantIdAndAnio(tid(), year);
    }

    public MetaComercial save(MetaComercial meta) {
        meta.setTenantId(tid());
        if (meta.getMontoAlcanzado() != null && meta.getMontoObjetivo() != null && meta.getMontoObjetivo().compareTo(BigDecimal.ZERO) > 0) {
            meta.setPorcentajeCumplimiento(
                meta.getMontoAlcanzado().multiply(BigDecimal.valueOf(100))
                   .divide(meta.getMontoObjetivo(), 2, RoundingMode.HALF_UP)
            );
        }
        return repository.save(meta);
    }

    public void delete(Long id) {
        repository.delete(repository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
