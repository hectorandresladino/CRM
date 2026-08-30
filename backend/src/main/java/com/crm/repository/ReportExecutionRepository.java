package com.crm.repository;

import com.crm.entity.ReportExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportExecutionRepository extends JpaRepository<ReportExecution, Long> {
    List<ReportExecution> findByTenantId(Long tenantId);
    List<ReportExecution> findByTenantIdAndReportId(Long tenantId, Long reportId);
}
