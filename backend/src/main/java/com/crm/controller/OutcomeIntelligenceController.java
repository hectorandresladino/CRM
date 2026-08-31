/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.service.OutcomeIntelligenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/outcomes")
@RequiredArgsConstructor
public class OutcomeIntelligenceController {

    private final OutcomeIntelligenceService service;

    @GetMapping("/scorecard")
    public ResponseEntity<Map<String, Object>> scorecard(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "90") Integer days) {
        return ResponseEntity.ok(service.scorecard(days));
    }
}
