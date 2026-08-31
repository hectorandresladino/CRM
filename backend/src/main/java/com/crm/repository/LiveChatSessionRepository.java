package com.crm.repository;

import com.crm.entity.LiveChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LiveChatSessionRepository extends JpaRepository<LiveChatSession, Long> {
    List<LiveChatSession> findByTenantId(Long tenantId);
    List<LiveChatSession> findByTenantIdAndStatus(Long tenantId, LiveChatSession.ChatStatus status);
    List<LiveChatSession> findByTenantIdAndAssignedAgentId(Long tenantId, Long agentId);
    Optional<LiveChatSession> findByTenantIdAndId(Long tenantId, Long id);
}
