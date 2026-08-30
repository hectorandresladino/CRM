/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.EmailTemplate;
import com.crm.security.TenantContext;
import com.crm.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

    private final EmailTemplateService emailTemplateService;

    @GetMapping
    public ResponseEntity<List<EmailTemplate>> getAll() {
        return ResponseEntity.ok(emailTemplateService.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<EmailTemplate> create(@RequestBody EmailTemplate template) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailTemplateService.save(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplate> update(@PathVariable Long id, @RequestBody EmailTemplate template) {
        return ResponseEntity.ok(emailTemplateService.update(id, template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emailTemplateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

