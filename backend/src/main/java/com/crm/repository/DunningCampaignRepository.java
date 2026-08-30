/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DunningCampaignRepository extends JpaRepository<DunningCampaign, Long> {
    List<DunningCampaign> findByTenantId(Long tenantId);
    List<DunningCampaign> findByTenantIdAndInvoiceId(Long tenantId, Long invoiceId);
    List<DunningCampaign> findByTenantIdAndStatus(Long tenantId, String status);
}
