/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.ProductoServicio;
import com.crm.security.TenantContext;
import com.crm.service.ProductoServicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoServicioController {
    private final ProductoServicioService service;

    @GetMapping
    public ResponseEntity<List<ProductoServicio>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @GetMapping("/familia/{familia}")
    public ResponseEntity<List<ProductoServicio>> getByFamilia(@PathVariable String familia) {
        return ResponseEntity.ok(service.findByFamilia(getCurrentTenantId(), familia));
    }

    @PostMapping
    public ResponseEntity<ProductoServicio> create(@RequestBody ProductoServicio producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoServicio> update(@PathVariable Long id, @RequestBody ProductoServicio producto) {
        producto.setId(id);
        return ResponseEntity.ok(service.save(producto));
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

