/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.ApiKey;
import com.crm.security.TenantContext;
import com.crm.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService service;

    @GetMapping
    public ResponseEntity<List<ApiKey>> getAll() {
        return ResponseEntity.ok(service.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<ApiKey> create(@RequestBody ApiKey apiKey) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(apiKey));
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

