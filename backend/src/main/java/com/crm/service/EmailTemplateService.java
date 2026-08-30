/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.service;

import com.crm.entity.EmailTemplate;
import com.crm.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    public List<EmailTemplate> findAll(Long tenantId) {
        return emailTemplateRepository.findByTenantId(tenantId);
    }

    public EmailTemplate save(EmailTemplate template) {
        return emailTemplateRepository.save(template);
    }

    public EmailTemplate update(Long id, EmailTemplate template) {
        EmailTemplate existing = emailTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
        existing.setName(template.getName());
        existing.setSubject(template.getSubject());
        existing.setBodyHtml(template.getBodyHtml());
        existing.setBodyText(template.getBodyText());
        existing.setCategory(template.getCategory());
        existing.setIsActive(template.getIsActive());
        return emailTemplateRepository.save(existing);
    }

    public void delete(Long id) {
        emailTemplateRepository.deleteById(id);
    }
}
