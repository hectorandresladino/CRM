/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.EmailMarketing;
import com.crm.repository.EmailMarketingRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailMarketingService {
    
    private final EmailMarketingRepository emailRepository;
    
    public List<EmailMarketing> findAll() {
        return emailRepository.findByTenantId(tid());
    }
    
    public Optional<EmailMarketing> findById(Long id) {
        return emailRepository.findByTenantIdAndId(tid(), id);
    }
    
    public EmailMarketing save(EmailMarketing email) {
        email.setTenantId(tid());
        return emailRepository.save(email);
    }
    
    public EmailMarketing update(Long id, EmailMarketing email) {
        emailRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));
        email.setId(id);
        email.setTenantId(tid());
        return emailRepository.save(email);
    }
    
    public void delete(Long id) {
        emailRepository.delete(emailRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Email no encontrado")));
    }
    
    public List<EmailMarketing> findByEstado(String estado) {
        return emailRepository.findByTenantIdAndEstado(tid(), estado);
    }
    
    public List<EmailMarketing> findByTipo(String tipo) {
        return emailRepository.findByTenantIdAndTipo(tid(), tipo);
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
