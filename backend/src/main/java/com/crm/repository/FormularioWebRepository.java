/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.FormularioWeb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormularioWebRepository extends JpaRepository<FormularioWeb, Long> {
    List<FormularioWeb> findByTenantId(Long tenantId);
    Optional<FormularioWeb> findByEmbedToken(String embedToken);
    Optional<FormularioWeb> findByTenantIdAndId(Long tenantId, Long id);
}
