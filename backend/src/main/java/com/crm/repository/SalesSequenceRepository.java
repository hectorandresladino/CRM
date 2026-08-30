package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesSequenceRepository extends JpaRepository<SalesSequence, Long> {
    List<SalesSequence> findByTenantId(Long tenantId);
    List<SalesSequence> findByTenantIdAndIsActive(Long tenantId, Boolean active);
}
