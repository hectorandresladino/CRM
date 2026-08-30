package com.crm.repository;

import com.crm.entity.IntegrationSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IntegrationSyncLogRepository extends JpaRepository<IntegrationSyncLog, Long> {
    List<IntegrationSyncLog> findByTenantId(Long tenantId);
    List<IntegrationSyncLog> findByTenantIdAndIntegrationId(Long tenantId, Long integrationId);
}
