/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.MesaAyuda;
import com.crm.service.MesaAyudaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesa-ayuda")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MesaAyudaController {
    
    private final MesaAyudaService mesaAyudaService;
    
    @GetMapping
    public List<MesaAyuda> findAll() {
        return mesaAyudaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MesaAyuda> findById(@PathVariable Long id) {
        return mesaAyudaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public MesaAyuda save(@RequestBody MesaAyuda ticket) {
        return mesaAyudaService.save(ticket);
    }
    
    @PutMapping("/{id}")
    public MesaAyuda update(@PathVariable Long id, @RequestBody MesaAyuda ticket) {
        return mesaAyudaService.update(id, ticket);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mesaAyudaService.delete(id);
    }
    
    @PutMapping("/{id}/asignar")
    public MesaAyuda asignar(@PathVariable Long id, @RequestBody String asignadoA) {
        return mesaAyudaService.asignar(id, asignadoA);
    }
    
    @PutMapping("/{id}/resolver")
    public MesaAyuda resolver(@PathVariable Long id, @RequestBody ResolverRequest request) {
        return mesaAyudaService.resolver(id, request.solucion, request.satisfaccion);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public List<MesaAyuda> findByClienteId(@PathVariable Long clienteId) {
        return mesaAyudaService.findByClienteId(clienteId);
    }
    
    @GetMapping("/estado/{estado}")
    public List<MesaAyuda> findByEstado(@PathVariable String estado) {
        return mesaAyudaService.findByEstado(estado);
    }
    
    @GetMapping("/asignado/{asignadoA}")
    public List<MesaAyuda> findByAsignadoA(@PathVariable String asignadoA) {
        return mesaAyudaService.findByAsignadoA(asignadoA);
    }
    
    @GetMapping("/abiertos")
    public List<MesaAyuda> findAbiertos() {
        return mesaAyudaService.findAbiertos();
    }
    
    static class ResolverRequest {
        public String solucion;
        public Integer satisfaccion;
    }
}
