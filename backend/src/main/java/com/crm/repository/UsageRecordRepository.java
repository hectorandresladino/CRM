package com.crm.repository;

import com.crm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    List<UsageRecord> findByTenantId(Long tenantId);
    List<UsageRecord> findByTenantIdAndSubscriptionId(Long tenantId, Long subscriptionId);
    List<UsageRecord> findByTenantIdAndIsBilled(Long tenantId, Boolean isBilled);
    List<UsageRecord> findByTenantIdAndMetricName(Long tenantId, String metricName);
}
