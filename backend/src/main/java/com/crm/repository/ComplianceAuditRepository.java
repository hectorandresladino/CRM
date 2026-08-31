package com.crm.repository;

import com.crm.entity.ComplianceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplianceAuditRepository extends JpaRepository<ComplianceAudit, Long> {
    List<ComplianceAudit> findByTenantId(Long tenantId);
    List<ComplianceAudit> findByTenantIdAndStandard(Long tenantId, ComplianceAudit.ComplianceStandard standard);
    Optional<ComplianceAudit> findByTenantIdAndId(Long tenantId, Long id);
}
