/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.Pago;
import com.crm.security.TenantContext;
import com.crm.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService service;

    @GetMapping
    public ResponseEntity<List<Pago>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.findByEstado(getCurrentTenantId(), estado));
    }

    @PostMapping
    public ResponseEntity<Pago> create(@RequestBody Pago pago) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(pago));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Pago> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(service.markAsPaid(id, body.get("transactionId")));
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

