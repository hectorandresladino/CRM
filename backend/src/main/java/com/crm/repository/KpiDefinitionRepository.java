package com.crm.repository;

import com.crm.entity.KpiDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface KpiDefinitionRepository extends JpaRepository<KpiDefinition, Long> {
    List<KpiDefinition> findByTenantId(Long tenantId);
    List<KpiDefinition> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
    Optional<KpiDefinition> findByTenantIdAndId(Long tenantId, Long id);
}
