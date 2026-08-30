package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldServiceOrderRepository extends JpaRepository<FieldServiceOrder, Long> {
    List<FieldServiceOrder> findByTenantId(Long tenantId);
    List<FieldServiceOrder> findByTenantIdAndClientId(Long tenantId, Long clientId);
    List<FieldServiceOrder> findByTenantIdAndStatus(Long tenantId, String status);
    List<FieldServiceOrder> findByTenantIdAndAssignedTechnician(Long tenantId, String technician);
}
