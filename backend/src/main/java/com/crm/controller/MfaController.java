/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.service.MfaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;

    @PostMapping("/setup")
    public ResponseEntity<Map<String, String>> setup(Authentication authentication,
                                                      @RequestBody(required = false) Map<String, String> body) {
        String currentCode = body == null ? null : body.get("code");
        return ResponseEntity.ok(mfaService.setupMfa(authentication.getName(), currentCode));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verify(Authentication authentication,
                                                        @RequestBody Map<String, String> body) {
        boolean valid = mfaService.verifyAndEnableMfa(authentication.getName(), body.get("code"));
        return ResponseEntity.ok(Map.of("verified", valid));
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validate(Authentication authentication,
                                                          @RequestBody Map<String, String> body) {
        boolean valid = mfaService.verifyCode(authentication.getName(), body.get("code"));
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/disable")
    public ResponseEntity<Void> disable(Authentication authentication,
                                         @RequestBody Map<String, String> body) {
        mfaService.disableMfa(authentication.getName(), body.get("code"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recovery-codes")
    public ResponseEntity<Map<String, List<String>>> recoveryCodes(Authentication authentication,
                                                                    @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("codes", mfaService.regenerateRecoveryCodes(
                authentication.getName(), body.get("code"))));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> status(Authentication authentication) {
        return ResponseEntity.ok(Map.of("mfaRequired", mfaService.isMfaRequired(authentication.getName())));
    }
}
