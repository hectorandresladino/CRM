/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.service.MfaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;

    @PostMapping("/setup")
    public ResponseEntity<Map<String, String>> setup(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(mfaService.setupMfa(body.get("username")));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verify(@RequestBody Map<String, String> body) {
        boolean valid = mfaService.verifyAndEnableMfa(body.get("username"), body.get("code"));
        return ResponseEntity.ok(Map.of("verified", valid));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validate(@RequestBody Map<String, String> body) {
        boolean valid = mfaService.verifyCode(body.get("username"), body.get("code"));
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable(@RequestBody Map<String, String> body) {
        mfaService.disableMfa(body.get("username"), body.get("code"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recovery-codes")
    public ResponseEntity<Map<String, List<String>>> recoveryCodes(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("codes", mfaService.regenerateRecoveryCodes(body.get("username"))));
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<Map<String, Boolean>> status(@PathVariable String username) {
        return ResponseEntity.ok(Map.of("mfaRequired", mfaService.isMfaRequired(username)));
    }
}
