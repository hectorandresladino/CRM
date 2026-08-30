package com.crm.repository;

import com.crm.entity.OfflineSyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OfflineSyncQueueRepository extends JpaRepository<OfflineSyncQueue, Long> {
    List<OfflineSyncQueue> findByTenantId(Long tenantId);
    List<OfflineSyncQueue> findByTenantIdAndStatus(Long tenantId, OfflineSyncQueue.SyncStatus status);
    List<OfflineSyncQueue> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<OfflineSyncQueue> findByTenantIdAndDeviceId(Long tenantId, Long deviceId);
}
