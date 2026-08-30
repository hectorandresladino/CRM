/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.CampanaMarketing;
import com.crm.service.CampanaMarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campanas-marketing")
@RequiredArgsConstructor
public class CampanaMarketingController {
    
    private final CampanaMarketingService campanaService;
    
    @GetMapping
    public List<CampanaMarketing> findAll() {
        return campanaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CampanaMarketing> findById(@PathVariable Long id) {
        return campanaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public CampanaMarketing save(@RequestBody CampanaMarketing campana) {
        return campanaService.save(campana);
    }
    
    @PutMapping("/{id}")
    public CampanaMarketing update(@PathVariable Long id, @RequestBody CampanaMarketing campana) {
        return campanaService.update(id, campana);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        campanaService.delete(id);
    }
    
    @GetMapping("/estado/{estado}")
    public List<CampanaMarketing> findByEstado(@PathVariable String estado) {
        return campanaService.findByEstado(estado);
    }
    
    @GetMapping("/tipo/{tipo}")
    public List<CampanaMarketing> findByTipo(@PathVariable String tipo) {
        return campanaService.findByTipo(tipo);
    }
}
