package com.crm.controller;

import com.crm.entity.*;
import com.crm.service.EnterpriseSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnterpriseSecurityController {

    private final EnterpriseSecurityService service;

    @GetMapping("/permission-sets")
    public ResponseEntity<List<PermissionSet>> getPermissionSets() { return ResponseEntity.ok(service.getPermissionSets()); }

    @PostMapping("/permission-sets")
    public ResponseEntity<PermissionSet> createPermissionSet(@RequestBody PermissionSet ps) { return ResponseEntity.ok(service.createPermissionSet(ps)); }

    @GetMapping("/sharing-rules")
    public ResponseEntity<List<SharingRule>> getSharingRules(@RequestParam(required = false) String objectName) { return ResponseEntity.ok(service.getSharingRules(objectName)); }

    @PostMapping("/sharing-rules")
    public ResponseEntity<SharingRule> createSharingRule(@RequestBody SharingRule rule) { return ResponseEntity.ok(service.createSharingRule(rule)); }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable Long userId) { return ResponseEntity.ok(service.getUserPermissions(userId)); }

    @GetMapping("/audit")
    public ResponseEntity<Map<String, Object>> getSecurityAudit() { return ResponseEntity.ok(service.getSecurityAudit()); }
}
