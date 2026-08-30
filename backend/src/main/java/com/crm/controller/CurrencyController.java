/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.security.TenantContext;
import com.crm.entity.CurrencyRate;
import com.crm.security.TenantContext;
import com.crm.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public ResponseEntity<List<CurrencyRate>> getAll() {
        return ResponseEntity.ok(currencyService.findAll(getCurrentTenantId()));
    }

    @PostMapping
    public ResponseEntity<CurrencyRate> save(@RequestBody CurrencyRate rate) {
        return ResponseEntity.ok(currencyService.save(rate));
    }

    @PostMapping("/fetch-rates")
    public ResponseEntity<Map<String, String>> fetchRates() {
        currencyService.fetchLatestRates(getCurrentTenantId());
        return ResponseEntity.ok(Map.of("status", "Rates updated successfully"));
    }

    @GetMapping("/convert")
    public ResponseEntity<Map<String, BigDecimal>> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {
        BigDecimal result = currencyService.convert(getCurrentTenantId(), from, to, amount);
        return ResponseEntity.ok(Map.of("result", result));
    }

    private Long getCurrentTenantId() {
        Long tid = TenantContext.getCurrentTenant();
        if (tid == null) throw new RuntimeException("No tenant context");
        return tid;
    }
}

