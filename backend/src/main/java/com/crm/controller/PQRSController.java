/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.PQRS;
import com.crm.service.PQRSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pqrs")
@RequiredArgsConstructor
public class PQRSController {
    
    private final PQRSService pqrsService;
    
    @GetMapping
    public List<PQRS> findAll() {
        return pqrsService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PQRS> findById(@PathVariable Long id) {
        return pqrsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public PQRS save(@RequestBody PQRS pqrs) {
        return pqrsService.save(pqrs);
    }
    
    @PutMapping("/{id}")
    public PQRS update(@PathVariable Long id, @RequestBody PQRS pqrs) {
        return pqrsService.update(id, pqrs);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pqrsService.delete(id);
    }
    
    @PutMapping("/{id}/resolver")
    public PQRS resolver(@PathVariable Long id, @RequestBody String resolucion) {
        return pqrsService.resolver(id, resolucion);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<PQRS> findByClienteId(@PathVariable Long clienteId) {
        return pqrsService.findByClienteId(clienteId);
    }
    
    @GetMapping("/estado/{estado}")
    public List<PQRS> findByEstado(@PathVariable String estado) {
        return pqrsService.findByEstado(estado);
    }
    
    @GetMapping("/prioridad/{prioridad}")
    public List<PQRS> findByPrioridad(@PathVariable String prioridad) {
        return pqrsService.findByPrioridad(prioridad);
    }
}
