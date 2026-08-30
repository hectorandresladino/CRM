package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionSetRepository extends JpaRepository<PermissionSet, Long> {
    List<PermissionSet> findByTenantId(Long tenantId);
    List<PermissionSet> findByTenantIdAndIsActive(Long tenantId, Boolean active);
}
