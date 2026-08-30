package com.crm.repository;

import com.crm.entity.FlowExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FlowExecutionLogRepository extends JpaRepository<FlowExecutionLog, Long> {
    List<FlowExecutionLog> findByTenantId(Long tenantId);
    List<FlowExecutionLog> findByTenantIdAndFlowId(Long tenantId, Long flowId);
    List<FlowExecutionLog> findByTenantIdAndStatus(Long tenantId, FlowExecutionLog.ExecutionStatus status);
}
