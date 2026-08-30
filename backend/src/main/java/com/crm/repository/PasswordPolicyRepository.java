package com.crm.repository;

import com.crm.entity.PasswordPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordPolicyRepository extends JpaRepository<PasswordPolicy, Long> {
    Optional<PasswordPolicy> findByTenantId(Long tenantId);
    Optional<PasswordPolicy> findByTenantIdAndIsActive(Long tenantId, Boolean isActive);
}
