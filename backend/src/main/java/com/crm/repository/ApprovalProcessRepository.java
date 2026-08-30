package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalProcessRepository extends JpaRepository<ApprovalProcess, Long> {
    List<ApprovalProcess> findByTenantId(Long tenantId);
    List<ApprovalProcess> findByTenantIdAndIsActive(Long tenantId, Boolean active);
}
