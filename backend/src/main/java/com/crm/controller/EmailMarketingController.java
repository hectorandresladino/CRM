/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.EmailMarketing;
import com.crm.service.EmailMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-marketing")
@RequiredArgsConstructor
public class EmailMarketingController {
    
    private final EmailMarketingService emailService;
    
    @GetMapping
    public List<EmailMarketing> findAll() {
        return emailService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmailMarketing> findById(@PathVariable Long id) {
        return emailService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public EmailMarketing save(@RequestBody EmailMarketing email) {
        return emailService.save(email);
    }
    
    @PutMapping("/{id}")
    public EmailMarketing update(@PathVariable Long id, @RequestBody EmailMarketing email) {
        return emailService.update(id, email);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        emailService.delete(id);
    }
    
    @GetMapping("/estado/{estado}")
    public List<EmailMarketing> findByEstado(@PathVariable String estado) {
        return emailService.findByEstado(estado);
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<EmailMarketing> findByTipo(@PathVariable String tipo) {
        return emailService.findByTipo(tipo);
    }
}
