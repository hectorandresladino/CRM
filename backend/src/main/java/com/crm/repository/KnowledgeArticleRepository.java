/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {
    List<KnowledgeArticle> findByTenantId(Long tenantId);
    List<KnowledgeArticle> findByTenantIdAndStatus(Long tenantId, String status);
    List<KnowledgeArticle> findByTenantIdAndCategory(Long tenantId, String category);
    Optional<KnowledgeArticle> findByTenantIdAndId(Long tenantId, Long id);
}
