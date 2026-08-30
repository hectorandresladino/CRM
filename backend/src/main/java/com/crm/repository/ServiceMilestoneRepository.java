package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceMilestoneRepository extends JpaRepository<ServiceMilestone, Long> {
    List<ServiceMilestone> findByTenantId(Long tenantId);
    List<ServiceMilestone> findByTenantIdAndEntitlementId(Long tenantId, Long entitlementId);
    List<ServiceMilestone> findByTenantIdAndIsViolated(Long tenantId, Boolean violated);
}
