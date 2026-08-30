package com.crm.repository;

import com.crm.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTenantId(Long tenantId);
    List<Notification> findByTenantIdAndUserId(Long tenantId, Long userId);
    List<Notification> findByTenantIdAndRead(Long tenantId, Boolean read);
}
