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
public class ExperienceCloudService {

    private final PortalConfigRepository portalRepo;

    public List<PortalConfig> getPortals() {
        return portalRepo.findByTenantId(TenantContext.getCurrentTenant());
    }

    public PortalConfig createPortal(PortalConfig config) {
        config.setTenantId(TenantContext.getCurrentTenant());
        return portalRepo.save(config);
    }

    public PortalConfig updatePortal(Long id, PortalConfig updated) {
        PortalConfig portal = portalRepo.findById(id).orElseThrow(() -> new RuntimeException("Portal no encontrado"));
        portal.setName(updated.getName());
        portal.setPortalType(updated.getPortalType());
        portal.setCustomDomain(updated.getCustomDomain());
        portal.setThemePrimaryColor(updated.getThemePrimaryColor());
        portal.setThemeSecondaryColor(updated.getThemeSecondaryColor());
        portal.setLogoUrl(updated.getLogoUrl());
        portal.setHeaderHtml(updated.getHeaderHtml());
        portal.setFooterHtml(updated.getFooterHtml());
        portal.setVisibleObjects(updated.getVisibleObjects());
        portal.setSelfServiceActions(updated.getSelfServiceActions());
        portal.setRequireLogin(updated.getRequireLogin());
        portal.setAllowRegistration(updated.getAllowRegistration());
        portal.setIsActive(updated.getIsActive());
        return portalRepo.save(portal);
    }

    public Map<String, Object> getPortalView(Long portalId) {
        PortalConfig portal = portalRepo.findById(portalId)
                .orElseThrow(() -> new RuntimeException("Portal no encontrado"));
        Map<String, Object> view = new HashMap<>();
        view.put("portal", portal);
        view.put("theme", Map.of(
                "primaryColor", portal.getThemePrimaryColor() != null ? portal.getThemePrimaryColor() : "#2563eb",
                "secondaryColor", portal.getThemeSecondaryColor() != null ? portal.getThemeSecondaryColor() : "#1e40af",
                "logoUrl", portal.getLogoUrl()
        ));
        view.put("selfServiceEnabled", true);
        view.put("availableActions", List.of("CREATE_TICKET", "VIEW_INVOICES", "UPDATE_PROFILE", "DOWNLOAD_REPORTS", "KNOWLEDGE_BASE"));
        return view;
    }
}
