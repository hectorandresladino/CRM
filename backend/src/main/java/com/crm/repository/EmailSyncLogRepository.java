/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.EmailSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailSyncLogRepository extends JpaRepository<EmailSyncLog, Long> {
    List<EmailSyncLog> findByTenantId(Long tenantId);
    List<EmailSyncLog> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<EmailSyncLog> findByTenantIdAndContactId(Long tenantId, Long contactId);
    List<EmailSyncLog> findByTenantIdAndAccountId(Long tenantId, Long accountId);
    List<EmailSyncLog> findByTenantIdAndIsIncoming(Long tenantId, Boolean isIncoming);
}
