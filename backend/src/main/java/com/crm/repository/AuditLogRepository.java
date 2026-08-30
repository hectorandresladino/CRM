/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AuditLog> findByEntityAndEntityIdOrderByCreatedAtDesc(String entity, Long entityId);
}
