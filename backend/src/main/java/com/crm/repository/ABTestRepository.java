package com.crm.repository;

import com.crm.entity.ABTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ABTestRepository extends JpaRepository<ABTest, Long> {
    List<ABTest> findByTenantId(Long tenantId);
    List<ABTest> findByTenantIdAndStatus(Long tenantId, ABTest.TestStatus status);
    Optional<ABTest> findByTenantIdAndId(Long tenantId, Long id);
}
