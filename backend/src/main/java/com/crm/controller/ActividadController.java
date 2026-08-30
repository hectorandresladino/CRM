/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Actividad;
import com.crm.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ActividadController {
    private final ActividadService service;

    @GetMapping
    public ResponseEntity<List<Actividad>> getAll() {
        return ResponseEntity.ok(service.findAll(1L));
    }

    @GetMapping("/usuario/{user}")
    public ResponseEntity<List<Actividad>> getByUser(@PathVariable String user) {
        return ResponseEntity.ok(service.findByUser(1L, user));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Actividad>> getByStatus(@PathVariable String estado) {
        return ResponseEntity.ok(service.findByStatus(1L, estado));
    }

    @PostMapping
    public ResponseEntity<Actividad> create(@RequestBody Actividad actividad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(actividad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actividad> update(@PathVariable Long id, @RequestBody Actividad actividad) {
        actividad.setId(id);
        return ResponseEntity.ok(service.save(actividad));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<Actividad> complete(@PathVariable Long id) {
        return ResponseEntity.ok(service.complete(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
