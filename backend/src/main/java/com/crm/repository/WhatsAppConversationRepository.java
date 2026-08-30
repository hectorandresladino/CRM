/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.repository;

import com.crm.entity.WhatsAppConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsAppConversationRepository extends JpaRepository<WhatsAppConversation, Long> {
    List<WhatsAppConversation> findByTenantIdOrderBySentAtDesc(Long tenantId);
    List<WhatsAppConversation> findByTenantIdAndContactPhone(Long tenantId, String contactPhone);
    List<WhatsAppConversation> findByTenantIdAndStatus(Long tenantId, WhatsAppConversation.ConversationStatus status);
}
