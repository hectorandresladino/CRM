/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.Tenant;
import com.crm.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WhiteLabelService {

    private final TenantRepository tenantRepository;

    public Map<String, Object> getBranding(Long tenantId) {
        Tenant t = tenantRepository.findById(tenantId).orElseThrow();
        Map<String, Object> branding = new LinkedHashMap<>();
        branding.put("tenantId", t.getId());
        branding.put("name", t.getName());
        branding.put("logoUrl", t.getLogoUrl());
        branding.put("primaryColor", t.getPrimaryColor());
        branding.put("customDomain", t.getCustomDomain());
        branding.put("locale", t.getLocale());
        branding.put("currency", t.getCurrency());
        branding.put("timezone", t.getTimezone());
        return branding;
    }

    public Map<String, Object> updateBranding(Long tenantId, Map<String, Object> branding) {
        Tenant t = tenantRepository.findById(tenantId).orElseThrow();

        if (branding.containsKey("logoUrl")) t.setLogoUrl((String) branding.get("logoUrl"));
        if (branding.containsKey("primaryColor")) t.setPrimaryColor((String) branding.get("primaryColor"));
        if (branding.containsKey("customDomain")) {
            String domain = (String) branding.get("customDomain");
            if (domain != null && !domain.isBlank()) {
                Optional<Tenant> existing = tenantRepository.findBySlug(domain);
                if (existing.isPresent() && !existing.get().getId().equals(tenantId)) {
                    throw new RuntimeException("Dominio ya en uso");
                }
                t.setCustomDomain(domain);
                t.setSlug(domain);
            }
        }
        if (branding.containsKey("locale")) t.setLocale((String) branding.get("locale"));
        if (branding.containsKey("currency")) t.setCurrency((String) branding.get("currency"));
        if (branding.containsKey("timezone")) t.setTimezone((String) branding.get("timezone"));

        tenantRepository.save(t);
        log.info("Branding updated for tenant {}", tenantId);
        return getBranding(tenantId);
    }

    public Map<String, Object> getPublicBranding(String domain) {
        Tenant t = tenantRepository.findBySlug(domain)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado para dominio: " + domain));
        Map<String, Object> branding = new LinkedHashMap<>();
        branding.put("name", t.getName());
        branding.put("logoUrl", t.getLogoUrl());
        branding.put("primaryColor", t.getPrimaryColor() != null ? t.getPrimaryColor() : "#2563eb");
        branding.put("locale", t.getLocale());
        branding.put("currency", t.getCurrency());
        return branding;
    }
}
