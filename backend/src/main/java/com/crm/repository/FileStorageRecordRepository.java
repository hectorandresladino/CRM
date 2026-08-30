package com.crm.repository;

import com.crm.entity.FileStorageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileStorageRecordRepository extends JpaRepository<FileStorageRecord, Long> {
    List<FileStorageRecord> findByTenantId(Long tenantId);
    List<FileStorageRecord> findByTenantIdAndEntityType(Long tenantId, String entityType);
    List<FileStorageRecord> findByTenantIdAndEntityId(Long tenantId, Long entityId);
}
