package com.crm.repository;

import com.crm.entity.PortalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortalConfigRepository extends JpaRepository<PortalConfig, Long> {
    List<PortalConfig> findByTenantId(Long tenantId);
    List<PortalConfig> findByTenantIdAndIsActive(Long tenantId, Boolean active);
}
