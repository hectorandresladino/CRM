/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.CPQQuoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CPQQuoteItemRepository extends JpaRepository<CPQQuoteItem, Long> {
    List<CPQQuoteItem> findByTenantId(Long tenantId);
    List<CPQQuoteItem> findByTenantIdAndCotizacionId(Long tenantId, Long cotizacionId);
    List<CPQQuoteItem> findByTenantIdAndApprovalRequired(Long tenantId, Boolean approvalRequired);
}
