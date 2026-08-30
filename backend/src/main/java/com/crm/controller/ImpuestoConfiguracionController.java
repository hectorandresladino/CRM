/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.ImpuestoConfiguracion;
import com.crm.security.TenantContext;
import com.crm.service.ImpuestoConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/impuestos")
@RequiredArgsConstructor
public class ImpuestoConfiguracionController {
    private final ImpuestoConfiguracionService service;

    @GetMapping
    public ResponseEntity<List<ImpuestoConfiguracion>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<ImpuestoConfiguracion>> getByPais(@PathVariable String pais) {
        return ResponseEntity.ok(service.findByPais(getCurrentTenantId(), pais));
    }

    @PostMapping
    public ResponseEntity<ImpuestoConfiguracion> create(@RequestBody ImpuestoConfiguracion impuesto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(impuesto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImpuestoConfiguracion> update(@PathVariable Long id, @RequestBody ImpuestoConfiguracion impuesto) {
        impuesto.setId(id);
        return ResponseEntity.ok(service.save(impuesto));
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

