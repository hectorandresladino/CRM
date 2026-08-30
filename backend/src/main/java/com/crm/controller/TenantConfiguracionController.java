/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.TenantConfiguracion;
import com.crm.security.TenantContext;
import com.crm.service.TenantConfiguracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant-config")
@RequiredArgsConstructor
public class TenantConfiguracionController {
    private final TenantConfiguracionService service;

    @GetMapping
    public ResponseEntity<TenantConfiguracion> get() {
        return ResponseEntity.ok(service.findByTenantId(getCurrentTenantId()));
    }

    @PutMapping
    public ResponseEntity<TenantConfiguracion> update(@RequestBody TenantConfiguracion config) {
        return ResponseEntity.ok(service.save(config));
    }

    @GetMapping("/next-factura")
    public ResponseEntity<String> nextFactura() {
        return ResponseEntity.ok(service.getNextFacturaNumber(getCurrentTenantId()));
    }

    @GetMapping("/next-cotizacion")
    public ResponseEntity<String> nextCotizacion() {
        return ResponseEntity.ok(service.getNextCotizacionNumber(getCurrentTenantId()));
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}
