package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, Long> {
    List<CustomerSegment> findByTenantId(Long tenantId);
    List<CustomerSegment> findByTenantIdAndIsActive(Long tenantId, Boolean active);
}
