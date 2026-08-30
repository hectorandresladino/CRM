/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.LeadScore;
import com.crm.security.TenantContext;
import com.crm.service.LeadScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lead-scores")
@RequiredArgsConstructor
public class LeadScoreController {

    private final LeadScoreService leadScoreService;

    @GetMapping
    public ResponseEntity<List<LeadScore>> getAll() {
        return ResponseEntity.ok(leadScoreService.findAll(getCurrentTenantId()));
    }

    @PostMapping("/prospecto/{prospectoId}/score")
    public ResponseEntity<LeadScore> scoreProspecto(@PathVariable Long prospectoId) {
        return ResponseEntity.ok(leadScoreService.scoreProspecto(getCurrentTenantId(), prospectoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leadScoreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

