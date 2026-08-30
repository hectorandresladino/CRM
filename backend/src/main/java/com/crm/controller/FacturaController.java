/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Factura;
import com.crm.service.FacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
public class FacturaController {
    
    private final FacturaService facturaService;
    
    @GetMapping
    public List<Factura> findAll() {
        return facturaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Factura> findById(@PathVariable Long id) {
        return facturaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Factura save(@RequestBody Factura factura) {
        return facturaService.save(factura);
    }
    
    @PutMapping("/{id}")
    public Factura update(@PathVariable Long id, @RequestBody Factura factura) {
        return facturaService.update(id, factura);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        facturaService.delete(id);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<Factura> findByClienteId(@PathVariable Long clienteId) {
        return facturaService.findByClienteId(clienteId);
    }
    
    @GetMapping("/estado/{estado}")
    public List<Factura> findByEstado(@PathVariable String estado) {
        return facturaService.findByEstado(estado);
    }
    
    @GetMapping("/vencidas")
    public List<Factura> findVencidas() {
        return facturaService.findVencidas();
    }
}
