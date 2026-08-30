package com.crm.repository;

import com.crm.entity.SessionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRecordRepository extends JpaRepository<SessionRecord, Long> {
    List<SessionRecord> findByTenantId(Long tenantId);
    List<SessionRecord> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<SessionRecord> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
