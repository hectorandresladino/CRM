package com.crm.repository;

import com.crm.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    List<Webhook> findByTenantIdAndEsActivo(Long tenantId, Boolean esActivo);
    List<Webhook> findByTenantIdAndEvento(Long tenantId, String evento);
}
