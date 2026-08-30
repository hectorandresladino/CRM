package com.crm.repository;

import com.crm.entity.WebhookLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WebhookLogRepository extends JpaRepository<WebhookLog, Long> {
    List<WebhookLog> findByTenantId(Long tenantId);
    List<WebhookLog> findByTenantIdAndWebhookId(Long tenantId, Long webhookId);
    List<WebhookLog> findByTenantIdAndStatus(Long tenantId, WebhookLog.DeliveryStatus status);
}
