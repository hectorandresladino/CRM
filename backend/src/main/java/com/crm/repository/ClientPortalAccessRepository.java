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
}
