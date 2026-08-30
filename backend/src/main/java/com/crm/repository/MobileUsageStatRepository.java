package com.crm.repository;

import com.crm.entity.MobileUsageStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MobileUsageStatRepository extends JpaRepository<MobileUsageStat, Long> {
    List<MobileUsageStat> findByTenantId(Long tenantId);
    List<MobileUsageStat> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<MobileUsageStat> findByTenantIdAndDeviceId(Long tenantId, Long deviceId);
}
