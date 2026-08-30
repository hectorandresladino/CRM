package com.crm.repository;

import com.crm.entity.KpiSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KpiSnapshotRepository extends JpaRepository<KpiSnapshot, Long> {
    List<KpiSnapshot> findByTenantId(Long tenantId);
    List<KpiSnapshot> findByTenantIdAndKpiId(Long tenantId, Long kpiId);
}
