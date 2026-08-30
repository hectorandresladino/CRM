/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.*;
import com.crm.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AgencyService {

    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsuarioRepository usuarioRepository;

    public Tenant createSubAccount(Long agencyTenantId, String name, String slug, String country, String currency) {
        Tenant agency = tenantRepository.findById(agencyTenantId)
                .orElseThrow(() -> new RuntimeException("Agencia no encontrada"));

        if (!Boolean.TRUE.equals(agency.getIsAgency())) {
            throw new RuntimeException("El tenant no es una agencia");
        }

        long subAccounts = tenantRepository.countByParentTenantId(agencyTenantId);
        Plan agencyPlan = planRepository.findById(agency.getPlanId()).orElseThrow();
        if (agencyPlan.getMaxSubAccounts() != null && subAccounts >= agencyPlan.getMaxSubAccounts()) {
            throw new RuntimeException("Limite de subcuentas alcanzado (" + agencyPlan.getMaxSubAccounts() + ")");
        }

        Tenant subAccount = new Tenant();
        subAccount.setName(name);
        subAccount.setSlug(slug);
        subAccount.setCountry(country);
        subAccount.setCurrency(currency);
        subAccount.setTimezone(agency.getTimezone());
        subAccount.setLocale(agency.getLocale());
        subAccount.setStatus(Tenant.TenantStatus.ACTIVE);
        subAccount.setPlanId(agency.getPlanId());
        subAccount.setParentTenantId(agencyTenantId);
        subAccount.setIsAgency(false);
        subAccount.setIsSubAccount(true);
        subAccount.setMaxUsers(null);
        subAccount.setMaxClients(agency.getMaxClients());
        subAccount.setMaxStorageMb(agency.getMaxStorageMb());
        return tenantRepository.save(subAccount);
    }

    public List<Map<String, Object>> getSubAccounts(Long agencyTenantId) {
        List<Tenant> subs = tenantRepository.findByParentTenantId(agencyTenantId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tenant t : subs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", t.getId());
            map.put("name", t.getName());
            map.put("slug", t.getSlug());
            map.put("status", t.getStatus());
            map.put("country", t.getCountry());
            map.put("currency", t.getCurrency());
            map.put("createdAt", t.getCreatedAt());
            long userCount = usuarioRepository.countByTenantId(t.getId());
            map.put("userCount", userCount);
            result.add(map);
        }
        return result;
    }

    public Map<String, Object> getAgencyDashboard(Long agencyTenantId) {
        List<Tenant> subs = tenantRepository.findByParentTenantId(agencyTenantId);
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalSubAccounts", subs.size());
        dashboard.put("activeSubAccounts", subs.stream().filter(t -> t.getStatus() == Tenant.TenantStatus.ACTIVE).count());
        dashboard.put("suspendedSubAccounts", subs.stream().filter(t -> t.getStatus() == Tenant.TenantStatus.SUSPENDED).count());
        dashboard.put("cancelledSubAccounts", subs.stream().filter(t -> t.getStatus() == Tenant.TenantStatus.CANCELLED).count());

        List<Map<String, Object>> subList = new ArrayList<>();
        for (Tenant sub : subs) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", sub.getId());
            info.put("name", sub.getName());
            info.put("status", sub.getStatus());
            info.put("users", usuarioRepository.countByTenantId(sub.getId()));
            subList.add(info);
        }
        dashboard.put("subAccounts", subList);
        return dashboard;
    }

    public void suspendSubAccount(Long agencyTenantId, Long subAccountId, String reason) {
        Tenant sub = tenantRepository.findById(subAccountId).orElseThrow();
        if (!agencyTenantId.equals(sub.getParentTenantId())) {
            throw new RuntimeException("La subcuenta no pertenece a esta agencia");
        }
        sub.setStatus(Tenant.TenantStatus.SUSPENDED);
        sub.setSuspendedAt(java.time.LocalDateTime.now());
        sub.setSuspendedReason(reason);
        tenantRepository.save(sub);
        log.info("Subcuenta {} suspendida por agencia {}", subAccountId, agencyTenantId);
    }

    public void deleteSubAccount(Long agencyTenantId, Long subAccountId) {
        Tenant sub = tenantRepository.findById(subAccountId).orElseThrow();
        if (!agencyTenantId.equals(sub.getParentTenantId())) {
            throw new RuntimeException("La subcuenta no pertenece a esta agencia");
        }
        sub.setStatus(Tenant.TenantStatus.CANCELLED);
        tenantRepository.save(sub);
        log.info("Subcuenta {} eliminada por agencia {}", subAccountId, agencyTenantId);
    }
}
