package com.crm.repository;

import com.crm.entity.GdprRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GdprRequestRepository extends JpaRepository<GdprRequest, Long> {
    List<GdprRequest> findByTenantId(Long tenantId);
    List<GdprRequest> findByTenantIdAndStatus(Long tenantId, GdprRequest.RequestStatus status);
    List<GdprRequest> findByTenantIdAndClientId(Long tenantId, Long clientId);
    Optional<GdprRequest> findByTenantIdAndId(Long tenantId, Long id);
}
