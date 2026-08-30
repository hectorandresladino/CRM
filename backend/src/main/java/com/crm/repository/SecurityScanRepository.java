package com.crm.repository;

import com.crm.entity.SecurityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SecurityScanRepository extends JpaRepository<SecurityScan, Long> {
    List<SecurityScan> findByTenantId(Long tenantId);
    List<SecurityScan> findByTenantIdAndStatus(Long tenantId, SecurityScan.ScanStatus status);
}
