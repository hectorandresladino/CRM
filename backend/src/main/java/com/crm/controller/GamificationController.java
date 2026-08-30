/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.GamificationBadge;
import com.crm.security.TenantContext;
import com.crm.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping
    public ResponseEntity<List<GamificationBadge>> getAll() {
        return ResponseEntity.ok(gamificationService.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<GamificationBadge> create(@RequestBody GamificationBadge badge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gamificationService.save(badge));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gamificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

