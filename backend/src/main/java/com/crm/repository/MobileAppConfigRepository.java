package com.crm.repository;

import com.crm.entity.MobileAppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MobileAppConfigRepository extends JpaRepository<MobileAppConfig, Long> {
    List<MobileAppConfig> findByTenantId(Long tenantId);
    Optional<MobileAppConfig> findByTenantIdAndConfigKey(Long tenantId, String configKey);
    List<MobileAppConfig> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
