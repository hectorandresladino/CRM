/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.WhatsAppBusiness;
import com.crm.repository.WhatsAppBusinessRepository;
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
        return whatsappRepository.findAll();
    }
    
    public Optional<WhatsAppBusiness> findById(Long id) {
        return whatsappRepository.findById(id);
    }
    
    public WhatsAppBusiness save(WhatsAppBusiness whatsapp) {
        return whatsappRepository.save(whatsapp);
    }
    
    public WhatsAppBusiness update(Long id, WhatsAppBusiness whatsapp) {
        whatsapp.setId(id);
        return whatsappRepository.save(whatsapp);
    }
    
    public void delete(Long id) {
        whatsappRepository.deleteById(id);
    }
    
    public List<WhatsAppBusiness> findByEstado(String estado) {
        return whatsappRepository.findByEstado(estado);
    }
    
    public List<WhatsAppBusiness> findByTelefono(String telefono) {
        return whatsappRepository.findByTelefono(telefono);
    }
    
    public List<WhatsAppBusiness> findNoLeidos() {
        return whatsappRepository.findByLeidoFalse();
    }
}
