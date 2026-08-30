/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.ClientPortalAccess;
import com.crm.security.TenantContext;
import com.crm.service.ClientPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/client-portal")
@RequiredArgsConstructor
public class ClientPortalController {

    private final ClientPortalService clientPortalService;

    @GetMapping
    public ResponseEntity<List<ClientPortalAccess>> getAll() {
        return ResponseEntity.ok(clientPortalService.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<ClientPortalAccess> createAccess(@RequestBody Map<String, Object> body) {
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        String email = body.get("email").toString();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientPortalService.createAccess(getCurrentTenantId(), clienteId, email));
    }

    @PostMapping("/login")
    public ResponseEntity<ClientPortalAccess> login(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(clientPortalService.login(body.get("portalToken")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/revoke")
    public ResponseEntity<Void> revoke(@PathVariable Long id) {
        clientPortalService.revoke(id);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

