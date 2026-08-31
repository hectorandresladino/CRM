/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.GdprConsent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GdprConsentRepository extends JpaRepository<GdprConsent, Long> {
    List<GdprConsent> findByTenantId(Long tenantId);
    List<GdprConsent> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    List<GdprConsent> findByTenantIdAndProspectoId(Long tenantId, Long prospectoId);
    Optional<GdprConsent> findByTenantIdAndId(Long tenantId, Long id);
}
