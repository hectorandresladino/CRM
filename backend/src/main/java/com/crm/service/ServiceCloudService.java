package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceCloudService {

    private final KnowledgeArticleRepository knowledgeRepo;
    private final EntitlementRepository entitlementRepo;
    private final ServiceMilestoneRepository milestoneRepo;
    private final FieldServiceOrderRepository fieldServiceRepo;

    public List<KnowledgeArticle> getArticles(String category) {
        Long tid = TenantContext.getCurrentTenant();
        if (category != null) return knowledgeRepo.findByTenantIdAndCategory(tid, category);
        return knowledgeRepo.findByTenantId(tid);
    }

    public List<KnowledgeArticle> getPublishedArticles() {
        return knowledgeRepo.findByTenantIdAndStatus(TenantContext.getCurrentTenant(), "PUBLISHED");
    }

    public KnowledgeArticle createArticle(KnowledgeArticle article) {
        article.setTenantId(TenantContext.getCurrentTenant());
        article.setStatus("DRAFT");
        return knowledgeRepo.save(article);
    }

    public KnowledgeArticle publishArticle(Long id) {
        KnowledgeArticle a = knowledgeRepo.findById(id).orElseThrow(() -> new RuntimeException("Articulo no encontrado"));
        a.setStatus("PUBLISHED");
        a.setPublishedAt(LocalDateTime.now());
        return knowledgeRepo.save(a);
    }

    public KnowledgeArticle voteArticle(Long id, boolean helpful) {
        KnowledgeArticle a = knowledgeRepo.findById(id).orElseThrow(() -> new RuntimeException("Articulo no encontrado"));
        a.setViewCount(a.getViewCount() + 1);
        if (helpful) a.setHelpfulCount(a.getHelpfulCount() + 1);
        else a.setNotHelpfulCount(a.getNotHelpfulCount() + 1);
        return knowledgeRepo.save(a);
    }

    public List<Entitlement> getEntitlements(Long clientId) {
        Long tid = TenantContext.getCurrentTenant();
        if (clientId != null) return entitlementRepo.findByTenantIdAndClientId(tid, clientId);
        return entitlementRepo.findByTenantId(tid);
    }

    public Entitlement createEntitlement(Entitlement e) {
        e.setTenantId(TenantContext.getCurrentTenant());
        return entitlementRepo.save(e);
    }

    public List<ServiceMilestone> getMilestones(Long entitlementId) {
        Long tid = TenantContext.getCurrentTenant();
        if (entitlementId != null) return milestoneRepo.findByTenantIdAndEntitlementId(tid, entitlementId);
        return milestoneRepo.findByTenantId(tid);
    }

    public ServiceMilestone createMilestone(ServiceMilestone m) {
        m.setTenantId(TenantContext.getCurrentTenant());
        m.setStartTime(LocalDateTime.now());
        if (m.getTargetMinutes() != null) {
            m.setTargetTime(LocalDateTime.now().plusMinutes(m.getTargetMinutes()));
        }
        return milestoneRepo.save(m);
    }

    public ServiceMilestone completeMilestone(Long id) {
        ServiceMilestone m = milestoneRepo.findById(id).orElseThrow(() -> new RuntimeException("Milestone no encontrado"));
        m.setCompletionTime(LocalDateTime.now());
        m.setIsAchieved(true);
        if (m.getStartTime() != null) {
            long minutes = java.time.Duration.between(m.getStartTime(), m.getCompletionTime()).toMinutes();
            m.setActualMinutes((int) minutes);
            if (m.getTargetTime() != null && m.getCompletionTime().isAfter(m.getTargetTime())) {
                m.setIsViolated(true);
                m.setIsAchieved(false);
            }
        }
        return milestoneRepo.save(m);
    }

    public List<FieldServiceOrder> getFieldServiceOrders(String status) {
        Long tid = TenantContext.getCurrentTenant();
        if (status != null) return fieldServiceRepo.findByTenantIdAndStatus(tid, status);
        return fieldServiceRepo.findByTenantId(tid);
    }

    public FieldServiceOrder createFieldServiceOrder(FieldServiceOrder order) {
        order.setTenantId(TenantContext.getCurrentTenant());
        return fieldServiceRepo.save(order);
    }

    public FieldServiceOrder updateFieldServiceStatus(Long id, String status) {
        FieldServiceOrder o = fieldServiceRepo.findById(id).orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        o.setStatus(status);
        if ("COMPLETED".equals(status)) o.setCompletedAt(LocalDateTime.now());
        return fieldServiceRepo.save(o);
    }

    public Map<String, Object> getOmnichannelRouting() {
        Long tid = TenantContext.getCurrentTenant();
        Map<String, Object> routing = new HashMap<>();
        routing.put("pendingCases", 0);
        routing.put("availableAgents", 0);
        routing.put("avgWaitTime", "0m");
        routing.put("routingStrategy", "SKILL_BASED");
        return routing;
    }
}
