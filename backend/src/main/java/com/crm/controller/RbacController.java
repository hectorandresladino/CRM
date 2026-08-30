/*
 * CRM SaaS - Copyright (c) 2024-2026 Hector Andres Ladino
 * Licensed under MIT License. See LICENSE file for details.
 */
package com.crm.controller;

import com.crm.entity.Usuario;
import com.crm.service.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rbac")
@RequiredArgsConstructor
public class RbacController {

    private final RbacService rbacService;

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        return ResponseEntity.ok(rbacService.getAllRolesPermissions());
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<Map<String, Object>> getRolePermissions(@PathVariable String role) {
        return ResponseEntity.ok(rbacService.getRolePermissions(Usuario.Role.valueOf(role)));
    }

    @GetMapping("/permissions/{username}")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable String username) {
        return ResponseEntity.ok(rbacService.getCurrentUserPermissions(username));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getTenantUsers() {
        return ResponseEntity.ok(rbacService.getTenantUsers());
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkPermission(
            @RequestParam String role,
            @RequestParam String module,
            @RequestParam String operation) {
        boolean has = rbacService.hasPermission(
            Usuario.Role.valueOf(role),
            RbacService.Module.valueOf(module),
            RbacService.Operation.valueOf(operation)
        );
        return ResponseEntity.ok(Map.of("allowed", has));
    }
}
