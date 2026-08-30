/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.BillingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingInvoiceRepository extends JpaRepository<BillingInvoice, Long> {

    List<BillingInvoice> findByTenantIdOrderByIssueDateDesc(Long tenantId);
    List<BillingInvoice> findByStatus(BillingInvoice.InvoiceStatus status);
    List<BillingInvoice> findByTenantIdAndStatus(Long tenantId, BillingInvoice.InvoiceStatus status);
}
