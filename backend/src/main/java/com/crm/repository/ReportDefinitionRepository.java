package com.crm.repository;

import com.crm.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, Long> {
    List<ReportDefinition> findByTenantId(Long tenantId);
    List<ReportDefinition> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    List<ReportDefinition> findByTenantIdAndIsShared(Long tenantId, Boolean isShared);
}
