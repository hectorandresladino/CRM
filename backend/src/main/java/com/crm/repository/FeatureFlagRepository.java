package com.crm.repository;

import com.crm.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    List<FeatureFlag> findByTenantId(Long tenantId);
    Optional<FeatureFlag> findByTenantIdAndKey(Long tenantId, String key);
    List<FeatureFlag> findByTenantIdAndIsEnabled(Long tenantId, Boolean isEnabled);
}
