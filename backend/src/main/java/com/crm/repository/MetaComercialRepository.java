/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.MetaComercial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaComercialRepository extends JpaRepository<MetaComercial, Long> {
    List<MetaComercial> findByTenantId(Long tenantId);
    List<MetaComercial> findByTenantIdAndAnio(Long tenantId, Integer anio);
    List<MetaComercial> findByTenantIdAndVendedor(Long tenantId, String vendedor);
}
