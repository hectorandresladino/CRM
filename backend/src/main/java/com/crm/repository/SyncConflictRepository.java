package com.crm.repository;

import com.crm.entity.SyncConflict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SyncConflictRepository extends JpaRepository<SyncConflict, Long> {
    List<SyncConflict> findByTenantId(Long tenantId);
    List<SyncConflict> findByTenantIdAndResolution(Long tenantId, SyncConflict.ResolutionStrategy resolution);
    List<SyncConflict> findByTenantIdAndUserId(Long tenantId, Long userId);
}
