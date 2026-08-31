package com.crm.repository;

import com.crm.entity.DataRetentionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataRetentionPolicyRepository extends JpaRepository<DataRetentionPolicy, Long> {
    List<DataRetentionPolicy> findByTenantId(Long tenantId);
    List<DataRetentionPolicy> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
    Optional<DataRetentionPolicy> findByTenantIdAndId(Long tenantId, Long id);
}
