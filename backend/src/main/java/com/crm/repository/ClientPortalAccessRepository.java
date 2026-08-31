/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.ClientPortalAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientPortalAccessRepository extends JpaRepository<ClientPortalAccess, Long> {
    List<ClientPortalAccess> findByTenantId(Long tenantId);
    Optional<ClientPortalAccess> findByTenantIdAndClienteId(Long tenantId, Long clienteId);
    Optional<ClientPortalAccess> findByPortalToken(String portalToken);
    Optional<ClientPortalAccess> findByTenantIdAndId(Long tenantId, Long id);
}
