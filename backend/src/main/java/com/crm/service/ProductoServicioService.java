/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.ProductoServicio;
import com.crm.repository.ProductoServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServicioService {
    private final ProductoServicioRepository repository;

    public List<ProductoServicio> findAll(Long tenantId) {
        return repository.findByTenantIdAndEsActivo(tenantId, true);
    }

    public List<ProductoServicio> findByFamilia(Long tenantId, String familia) {
        return repository.findByTenantIdAndFamilia(tenantId, familia);
    }

    public ProductoServicio save(ProductoServicio producto) {
        return repository.save(producto);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
