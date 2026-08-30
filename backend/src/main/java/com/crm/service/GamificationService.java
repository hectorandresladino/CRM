package com.crm.service;

import com.crm.entity.GamificationBadge;
import com.crm.repository.GamificationBadgeRepository;
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
        return gamificationBadgeRepository.findByTenantId(tenantId);
    }

    public GamificationBadge save(GamificationBadge badge) {
        return gamificationBadgeRepository.save(badge);
    }

    public void delete(Long id) {
        gamificationBadgeRepository.deleteById(id);
    }
}
