/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Contrato;
import com.crm.service.ContratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contratos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContratoController {
    
    private final ContratoService contratoService;
    
    @GetMapping
    public List<Contrato> findAll() {
        return contratoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Contrato> findById(@PathVariable Long id) {
        return contratoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Contrato save(@RequestBody Contrato contrato) {
        return contratoService.save(contrato);
    }
    
    @PutMapping("/{id}")
    public Contrato update(@PathVariable Long id, @RequestBody Contrato contrato) {
        return contratoService.update(id, contrato);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contratoService.delete(id);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<Contrato> findByClienteId(@PathVariable Long clienteId) {
        return contratoService.findByClienteId(clienteId);
    }
    
    @GetMapping("/estado/{estado}")
    public List<Contrato> findByEstado(@PathVariable String estado) {
        return contratoService.findByEstado(estado);
    }
    
    @GetMapping("/por-vencer")
    public List<Contrato> findPorVencer() {
        return contratoService.findPorVencer();
    }
}
