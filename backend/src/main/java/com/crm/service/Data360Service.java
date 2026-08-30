/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class Data360Service {

    private final UnifiedProfileRepository profileRepo;
    private final CustomerEventRepository eventRepo;
    private final CustomerSegmentRepository segmentRepo;

    public UnifiedProfile resolveIdentity(String email, String phone, String name, String company) {
        Long tid = TenantContext.getCurrentTenant();
        Optional<UnifiedProfile> existing = Optional.empty();
        if (email != null && !email.isEmpty()) {
            existing = profileRepo.findByTenantIdAndPrimaryEmail(tid, email);
        }
        if (existing.isEmpty() && phone != null && !phone.isEmpty()) {
            existing = profileRepo.findByTenantIdAndPrimaryPhone(tid, phone);
        }
        if (existing.isPresent()) {
            UnifiedProfile p = existing.get();
            if (email != null && (p.getPrimaryEmail() == null || p.getPrimaryEmail().isEmpty())) p.setPrimaryEmail(email);
            if (phone != null && (p.getPrimaryPhone() == null || p.getPrimaryPhone().isEmpty())) p.setPrimaryPhone(phone);
            if (name != null && (p.getFullName() == null || p.getFullName().isEmpty())) p.setFullName(name);
            if (company != null && (p.getCompany() == null || p.getCompany().isEmpty())) p.setCompany(company);
            return profileRepo.save(p);
        }

        UnifiedProfile profile = new UnifiedProfile();
        profile.setTenantId(tid);
        profile.setProfileUuid(UUID.randomUUID().toString());
        profile.setPrimaryEmail(email);
        profile.setPrimaryPhone(phone);
        profile.setFullName(name);
        profile.setCompany(company);
        profile.setMatchConfidence(1.0);
        profile.setIdentitySources("WEB");
        profile.setLifecycleStage("VISITOR");
        return profileRepo.save(profile);
    }

    public CustomerEvent ingestEvent(CustomerEvent event) {
        event.setTenantId(TenantContext.getCurrentTenant());
        if (event.getUnifiedProfileId() == null && event.getClientId() != null) {
            List<CustomerEvent> prior = eventRepo.findByTenantIdAndClientId(event.getTenantId(), event.getClientId());
            if (!prior.isEmpty()) event.setUnifiedProfileId(prior.get(0).getUnifiedProfileId());
        }
        CustomerEvent saved = eventRepo.save(event);
        if (saved.getUnifiedProfileId() != null) {
            profileRepo.findByTenantIdAndProfileUuid(saved.getTenantId(), saved.getUnifiedProfileId())
                    .ifPresent(p -> {
                        p.setTotalEvents(p.getTotalEvents() + 1);
                        p.setLastEventAt(saved.getEventTimestamp());
                        profileRepo.save(p);
                    });
        }
        return saved;
    }

    public List<CustomerEvent> getEvents(Long clientId, String eventType) {
        Long tid = TenantContext.getCurrentTenant();
        if (clientId != null) return eventRepo.findByTenantIdAndClientId(tid, clientId);
        if (eventType != null) return eventRepo.findByTenantIdAndEventType(tid, eventType);
        return eventRepo.findByTenantId(tid);
    }

    public List<UnifiedProfile> getProfiles(String lifecycleStage) {
        Long tid = TenantContext.getCurrentTenant();
        if (lifecycleStage != null) return profileRepo.findByTenantIdAndLifecycleStage(tid, lifecycleStage);
        return profileRepo.findByTenantId(tid);
    }

    public UnifiedProfile updateLifecycleStage(Long profileId, String stage) {
        UnifiedProfile p = profileRepo.findById(profileId).orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        p.setLifecycleStage(stage);
        return profileRepo.save(p);
    }

    public List<CustomerSegment> getSegments() {
        return segmentRepo.findByTenantIdAndIsActive(TenantContext.getCurrentTenant(), true);
    }

    public CustomerSegment createSegment(CustomerSegment segment) {
        segment.setTenantId(TenantContext.getCurrentTenant());
        return segmentRepo.save(segment);
    }

    public Map<String, Object> getProfile360(Long profileId) {
        UnifiedProfile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        Map<String, Object> view = new HashMap<>();
        view.put("profile", profile);
        if (profile.getProfileUuid() != null) {
            view.put("events", eventRepo.findByTenantIdAndUnifiedProfileId(profile.getTenantId(), profile.getProfileUuid()));
        }
        view.put("segments", segmentRepo.findByTenantIdAndIsActive(profile.getTenantId(), true));
        return view;
    }
}
