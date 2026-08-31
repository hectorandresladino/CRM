/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.ProductoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoServicioRepository extends JpaRepository<ProductoServicio, Long> {
    List<ProductoServicio> findByTenantIdAndEsActivo(Long tenantId, Boolean esActivo);
    Optional<ProductoServicio> findByTenantIdAndCodigo(Long tenantId, String codigo);
    List<ProductoServicio> findByTenantIdAndFamilia(Long tenantId, String familia);
    Optional<ProductoServicio> findByTenantIdAndId(Long tenantId, Long id);
}
