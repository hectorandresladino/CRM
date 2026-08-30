package com.crm.repository;

import com.crm.entity.RateLimitConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitConfigRepository extends JpaRepository<RateLimitConfig, Long> {
    List<RateLimitConfig> findByTenantId(Long tenantId);
    Optional<RateLimitConfig> findByTenantIdAndEndpoint(Long tenantId, String endpoint);
    List<RateLimitConfig> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
