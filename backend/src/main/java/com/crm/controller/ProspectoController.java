/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Prospecto;
import com.crm.service.ProspectoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prospectos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProspectoController {
    
    private final ProspectoService prospectoService;
    
    @GetMapping
    public ResponseEntity<List<Prospecto>> getAllProspectos() {
        return ResponseEntity.ok(prospectoService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Prospecto> getProspectoById(@PathVariable Long id) {
        return prospectoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Prospecto> createProspecto(@RequestBody Prospecto prospecto) {
        try {
            Prospecto nuevoProspecto = prospectoService.save(prospecto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProspecto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Prospecto> updateProspecto(@PathVariable Long id, @RequestBody Prospecto prospecto) {
        try {
            Prospecto prospectoActualizado = prospectoService.update(id, prospecto);
            return ResponseEntity.ok(prospectoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProspecto(@PathVariable Long id) {
        try {
            prospectoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Prospecto>> getProspectosByEstado(@PathVariable Prospecto.EstadoProspecto estado) {
        return ResponseEntity.ok(prospectoService.findByEstado(estado));
    }
    
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<Prospecto>> getProspectosByPrioridad(@PathVariable Prospecto.PrioridadProspecto prioridad) {
        return ResponseEntity.ok(prospectoService.findByPrioridad(prioridad));
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Prospecto>> buscarProspectos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String empresa) {
        if (empresa != null) {
            return ResponseEntity.ok(prospectoService.buscarPorEmpresa(empresa));
        }
        return ResponseEntity.ok(prospectoService.buscarPorNombre(nombre != null ? nombre : "", 
                                                                   apellido != null ? apellido : ""));
    }
    
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Prospecto> actualizarEstado(@PathVariable Long id, 
                                                      @RequestParam Prospecto.EstadoProspecto estado) {
        try {
            return ResponseEntity.ok(prospectoService.actualizarEstado(id, estado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
