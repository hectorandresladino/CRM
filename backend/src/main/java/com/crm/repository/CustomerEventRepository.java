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
public interface CustomerEventRepository extends JpaRepository<CustomerEvent, Long> {
    List<CustomerEvent> findByTenantId(Long tenantId);
    List<CustomerEvent> findByTenantIdAndClientId(Long tenantId, Long clientId);
    List<CustomerEvent> findByTenantIdAndEventType(Long tenantId, String eventType);
    List<CustomerEvent> findByTenantIdAndUnifiedProfileId(Long tenantId, String profileId);
}
