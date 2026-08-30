/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.MetaComercial;
import com.crm.security.TenantContext;
import com.crm.service.MetaComercialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
public class MetaComercialController {
    private final MetaComercialService service;

    @GetMapping
    public ResponseEntity<List<MetaComercial>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @GetMapping("/anio/{anio}")
    public ResponseEntity<List<MetaComercial>> getByYear(@PathVariable Integer anio) {
        return ResponseEntity.ok(service.findByYear(getCurrentTenantId(), anio));
    }

    @PostMapping
    public ResponseEntity<MetaComercial> create(@RequestBody MetaComercial meta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(meta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetaComercial> update(@PathVariable Long id, @RequestBody MetaComercial meta) {
        meta.setId(id);
        return ResponseEntity.ok(service.save(meta));
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

