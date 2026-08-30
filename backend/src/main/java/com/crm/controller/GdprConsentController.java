/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.GdprConsent;
import com.crm.service.GdprConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gdpr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GdprConsentController {

    private final GdprConsentService gdprConsentService;

    @GetMapping
    public ResponseEntity<List<GdprConsent>> getAll() {
        return ResponseEntity.ok(gdprConsentService.findAll(1L));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<GdprConsent>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(gdprConsentService.findByCliente(1L, clienteId));
    }

    @PostMapping
    public ResponseEntity<GdprConsent> grant(@RequestBody GdprConsent consent) {
        return ResponseEntity.ok(gdprConsentService.grant(consent));
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<GdprConsent> withdraw(@PathVariable Long id) {
        return ResponseEntity.ok(gdprConsentService.withdraw(id));
    }

    @DeleteMapping("/cliente/{clienteId}/purge")
    public ResponseEntity<Void> purgeClientData(@PathVariable Long clienteId) {
        gdprConsentService.deleteAllForCliente(1L, clienteId);
        return ResponseEntity.noContent().build();
    }
}
