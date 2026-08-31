/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.CampanaMarketing;
import com.crm.repository.CampanaMarketingRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampanaMarketingService {
    
    private final CampanaMarketingRepository campanaRepository;
    
    public List<CampanaMarketing> findAll() {
        return campanaRepository.findByTenantId(tid());
    }
    
    public Optional<CampanaMarketing> findById(Long id) {
        return campanaRepository.findByTenantIdAndId(tid(), id);
    }
    
    public CampanaMarketing save(CampanaMarketing campana) {
        campana.setTenantId(tid());
        return campanaRepository.save(campana);
    }
    
    public CampanaMarketing update(Long id, CampanaMarketing campana) {
        campanaRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada"));
        campana.setId(id);
        campana.setTenantId(tid());
        return campanaRepository.save(campana);
    }
    
    public void delete(Long id) {
        campanaRepository.delete(campanaRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada")));
    }
    
    public List<CampanaMarketing> findByEstado(String estado) {
        return campanaRepository.findByTenantIdAndEstado(tid(), estado);
    }
    
    public List<CampanaMarketing> findByTipo(String tipo) {
        return campanaRepository.findByTenantIdAndTipo(tid(), tipo);
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
