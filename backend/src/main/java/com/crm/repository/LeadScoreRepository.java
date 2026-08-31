/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.LeadScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadScoreRepository extends JpaRepository<LeadScore, Long> {
    List<LeadScore> findByTenantId(Long tenantId);
    Optional<LeadScore> findByTenantIdAndProspectoId(Long tenantId, Long prospectoId);
    List<LeadScore> findByTenantIdOrderByScoreDesc(Long tenantId);
    Optional<LeadScore> findByTenantIdAndId(Long tenantId, Long id);
}
