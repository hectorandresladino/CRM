package com.crm.repository;

import com.crm.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    List<ScheduledJob> findByTenantId(Long tenantId);
    List<ScheduledJob> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
    Optional<ScheduledJob> findByTenantIdAndId(Long tenantId, Long id);
}
