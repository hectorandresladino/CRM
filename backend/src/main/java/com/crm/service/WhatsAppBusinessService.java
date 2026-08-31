/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.WhatsAppBusiness;
import com.crm.repository.WhatsAppBusinessRepository;
import com.crm.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WhatsAppBusinessService {
    
    private final WhatsAppBusinessRepository whatsappRepository;
    
    public List<WhatsAppBusiness> findAll() {
        return whatsappRepository.findByTenantId(tid());
    }
    
    public Optional<WhatsAppBusiness> findById(Long id) {
        return whatsappRepository.findByTenantIdAndId(tid(), id);
    }
    
    public WhatsAppBusiness save(WhatsAppBusiness whatsapp) {
        whatsapp.setTenantId(tid());
        return whatsappRepository.save(whatsapp);
    }
    
    public WhatsAppBusiness update(Long id, WhatsAppBusiness whatsapp) {
        whatsappRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        whatsapp.setId(id);
        whatsapp.setTenantId(tid());
        return whatsappRepository.save(whatsapp);
    }
    
    public void delete(Long id) {
        whatsappRepository.delete(whatsappRepository.findByTenantIdAndId(tid(), id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado")));
    }
    
    public List<WhatsAppBusiness> findByEstado(String estado) {
        return whatsappRepository.findByTenantIdAndEstado(tid(), estado);
    }
    
    public List<WhatsAppBusiness> findByTelefono(String telefono) {
        return whatsappRepository.findByTenantIdAndTelefono(tid(), telefono);
    }
    
    public List<WhatsAppBusiness> findNoLeidos() {
        return whatsappRepository.findByTenantIdAndLeidoFalse(tid());
    }

    private Long tid() { return TenantContext.requireCurrentTenant(); }
}
