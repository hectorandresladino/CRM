/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.GamificationBadge;
import com.crm.repository.GamificationBadgeRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GamificationService {

    private final GamificationBadgeRepository gamificationBadgeRepository;

    public List<GamificationBadge> findAll(Long tenantId) {
        return gamificationBadgeRepository.findByTenantId(tid());
    }

    public GamificationBadge save(GamificationBadge badge) {
        badge.setTenantId(tid());
        return gamificationBadgeRepository.save(badge);
    }

    public void delete(Long id) {
        gamificationBadgeRepository.delete(gamificationBadgeRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Insignia no encontrada")));
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
