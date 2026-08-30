/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.CampoPersonalizado;
import com.crm.security.TenantContext;
import com.crm.service.CampoPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campos-personalizados")
@RequiredArgsConstructor
public class CampoPersonalizadoController {
    private final CampoPersonalizadoService service;

    @GetMapping("/entidad/{entidad}")
    public ResponseEntity<List<CampoPersonalizado>> getByEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(service.findByEntidad(getCurrentTenantId(), entidad));
    }

    @PostMapping
    public ResponseEntity<CampoPersonalizado> create(@RequestBody CampoPersonalizado campo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(campo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampoPersonalizado> update(@PathVariable Long id, @RequestBody CampoPersonalizado campo) {
        campo.setId(id);
        return ResponseEntity.ok(service.save(campo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

