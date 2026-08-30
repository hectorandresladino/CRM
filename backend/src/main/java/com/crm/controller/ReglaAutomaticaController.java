/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.ReglaAutomatica;
import com.crm.service.ReglaAutomaticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reglas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReglaAutomaticaController {
    private final ReglaAutomaticaService service;

    @GetMapping
    public ResponseEntity<List<ReglaAutomatica>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @PostMapping
    public ResponseEntity<ReglaAutomatica> create(@RequestBody ReglaAutomatica regla) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(regla));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReglaAutomatica> update(@PathVariable Long id, @RequestBody ReglaAutomatica regla) {
        regla.setId(id);
        return ResponseEntity.ok(service.save(regla));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ReglaAutomatica> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggle(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
