/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.SLAConfiguracion;
import com.crm.security.TenantContext;
import com.crm.service.SLAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla")
@RequiredArgsConstructor
public class SLAController {
    private final SLAService service;

    @GetMapping
    public ResponseEntity<List<SLAConfiguracion>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<SLAConfiguracion> create(@RequestBody SLAConfiguracion sla) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(sla));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SLAConfiguracion> update(@PathVariable Long id, @RequestBody SLAConfiguracion sla) {
        sla.setId(id);
        return ResponseEntity.ok(service.save(sla));
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

