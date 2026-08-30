package com.crm.repository;

import com.crm.entity.WhatsAppAIConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhatsAppAIConfigRepository extends JpaRepository<WhatsAppAIConfig, Long> {
    Optional<WhatsAppAIConfig> findByTenantId(Long tenantId);
}
