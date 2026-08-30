package com.crm.repository;

import com.crm.entity.SsoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SsoConfigRepository extends JpaRepository<SsoConfig, Long> {
    List<SsoConfig> findByTenantId(Long tenantId);
    Optional<SsoConfig> findByTenantIdAndProvider(Long tenantId, SsoConfig.SsoProvider provider);
    Optional<SsoConfig> findByTenantIdAndIsEnabled(Long tenantId, Boolean isEnabled);
}
