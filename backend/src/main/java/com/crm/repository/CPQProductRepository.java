/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.CPQProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CPQProductRepository extends JpaRepository<CPQProduct, Long> {
    List<CPQProduct> findByTenantId(Long tenantId);
    List<CPQProduct> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
    Optional<CPQProduct> findByTenantIdAndSku(Long tenantId, String sku);
}
