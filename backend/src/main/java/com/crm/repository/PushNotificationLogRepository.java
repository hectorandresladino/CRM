package com.crm.repository;

import com.crm.entity.PushNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PushNotificationLogRepository extends JpaRepository<PushNotificationLog, Long> {
    List<PushNotificationLog> findByTenantId(Long tenantId);
    List<PushNotificationLog> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<PushNotificationLog> findByTenantIdAndStatus(Long tenantId, PushNotificationLog.DeliveryStatus status);
}
