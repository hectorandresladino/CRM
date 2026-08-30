package com.crm.repository;

import com.crm.entity.DataExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DataExportRepository extends JpaRepository<DataExport, Long> {
    List<DataExport> findByTenantId(Long tenantId);
    List<DataExport> findByTenantIdAndStatus(Long tenantId, DataExport.ExportStatus status);
}
