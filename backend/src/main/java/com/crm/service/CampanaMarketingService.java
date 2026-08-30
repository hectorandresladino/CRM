/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.CampanaMarketing;
import com.crm.repository.CampanaMarketingRepository;
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
        return campanaRepository.findAll();
    }
    
    public Optional<CampanaMarketing> findById(Long id) {
        return campanaRepository.findById(id);
    }
    
    public CampanaMarketing save(CampanaMarketing campana) {
        return campanaRepository.save(campana);
    }
    
    public CampanaMarketing update(Long id, CampanaMarketing campana) {
        campana.setId(id);
        return campanaRepository.save(campana);
    }
    
    public void delete(Long id) {
        campanaRepository.deleteById(id);
    }
    
    public List<CampanaMarketing> findByEstado(String estado) {
        return campanaRepository.findByEstado(estado);
    }
    
    public List<CampanaMarketing> findByTipo(String tipo) {
        return campanaRepository.findByTipo(tipo);
    }
}
