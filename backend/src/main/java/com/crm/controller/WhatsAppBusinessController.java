/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.WhatsAppBusiness;
import com.crm.service.WhatsAppBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/whatsapp-business")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WhatsAppBusinessController {
    
    private final WhatsAppBusinessService whatsappService;
    
    @GetMapping
    public List<WhatsAppBusiness> findAll() {
        return whatsappService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<WhatsAppBusiness> findById(@PathVariable Long id) {
        return whatsappService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public WhatsAppBusiness save(@RequestBody WhatsAppBusiness whatsapp) {
        return whatsappService.save(whatsapp);
    }
    
    @PutMapping("/{id}")
    public WhatsAppBusiness update(@PathVariable Long id, @RequestBody WhatsAppBusiness whatsapp) {
        return whatsappService.update(id, whatsapp);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        whatsappService.delete(id);
    }
    
    @GetMapping("/estado/{estado}")
    public List<WhatsAppBusiness> findByEstado(@PathVariable String estado) {
        return whatsappService.findByEstado(estado);
    }
    
    @GetMapping("/telefono/{telefono}")
    public List<WhatsAppBusiness> findByTelefono(@PathVariable String telefono) {
        return whatsappService.findByTelefono(telefono);
    }
    
    @GetMapping("/no-leidos")
    public List<WhatsAppBusiness> findNoLeidos() {
        return whatsappService.findNoLeidos();
    }
}
