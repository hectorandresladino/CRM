package com.crm.repository;

import com.crm.entity.BackupRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BackupRecordRepository extends JpaRepository<BackupRecord, Long> {
    List<BackupRecord> findByTenantId(Long tenantId);
    List<BackupRecord> findByTenantIdAndStatus(Long tenantId, BackupRecord.BackupStatus status);
}
